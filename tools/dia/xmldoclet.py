# import json-doclet's output as UML classes
import dia
import xml.etree.ElementTree as ET

visibility = {'public':0, 'private':1, 'protected':2};
inheritance = {'pure-virtual':0, 'virtual':1, 'non-virtual':2}

def parse_html_color(s):
    return tuple(int(s[i:i+2], 16)/255.0 for i in (1,3,5))

default_color = parse_html_color("#FFDEC2")

def fix_kerning(s):
    return u"\u200B".join(s)

def parse_bool(s):
    return s.strip() == 'true'

#def transform_field(**field):
    #return (field["name"],
            #field["type"],
            #"",  # value
            #field["comment-text"],
            #visibility[field["visibility"]],
            #0,
            #field["static"],
            #)

def text_or(el, default=""):
    return el.text if el is not None else default

def transform_type(typ, simple=True):
    res = typ.find("qualifiedName").text + (typ.find("dimension").text or "")
    if simple: res = res.split(".")[-1]
    return res

def transform_param(param, vararg=False):
    dim = "[]" if vararg else ""
    return (param.find("name").text,
            transform_type(param.find("type")) + dim,
            None,
            text_or(param.find("comment")),
            0,
            )

def transform_method(meth, abstract=False):
    abstract = abstract or parse_bool(meth.find("isAbstract").text)
    return_type = transform_type(meth.find("result").find("type"))
    if return_type == "void": return_type = ""
    params = list(meth.iter("parameter"))
    varargs = parse_bool(meth.find("isVarArgs").text)
    return (meth.find("name").text,
            return_type,
            meth.find("comment").text,
            "",  # stereotype
            visibility[meth.find("scope").text],
            inheritance["pure-virtual" if abstract else "non-virtual"],
            parse_bool(meth.find("isFinal").text),
            parse_bool(meth.find("isStatic").text),
            [transform_param(params[i], varargs and i == len(params)-1) for i in xrange(len(params))],
            )

def add_class(cls, layer):
    o, _, _ = dia.get_object_type("UML - Class").create(0,0)
    o.properties["name"] = fix_kerning(cls.find("name").text)
    abstr = cls.find("isAbstract")
    interface = abstr is None # if this isn't present, cls is an interface
    abstract = interface or parse_bool(abstr.text)
    o.properties["abstract"] = abstract
    #o.properties["attributes"] = [transform_field(name=k, **v)
                                  #for k, v in cls["fields"].items()]
    o.properties["operations"] = [transform_method(m, interface) for m in cls.iter("method")]
    o.properties["fill_colour"] = default_color
    layer.add_object(o)
    return o

def add_extends(child, parent, layer):
    con, h1, h2 = dia.get_object_type("UML - Generalization").create(0,0)
    layer.add_object(con)
    h1.connect(parent.connections[6])
    h2.connect(child.connections[1])

def import_javadoc(obj, layer):
    classes = list(obj.iter("class")) + list(obj.iter("interface"))
    objects = { cls.find("qualifiedName").text : (cls, add_class(cls, layer))
                for cls in classes }
    for cls, obj in objects.values():
        bases = []
        ifaces = cls.find('interfaces')
        if ifaces is not None: bases += [i.text for i in ifaces.iter("string")]
        supclass = cls.find("superClass")
        if supclass is not None: bases.append(supclass.text)
        for base in bases:
            if base not in objects: continue
            _, parent = objects[base]
            add_extends(obj, parent, layer)
    layer.update_extents()

def load_xml(fn):
    return ET.parse(fn).getroot()

def is_class(obj):
    return "UML - Class" == str(obj.type)

def change_class_colors(html_col, layer=None):
    layer = layer or dia.active_display().diagram.data.active_layer
    color = parse_html_color(html_col)
    for o in (layer.objects[i] for i in xrange(len(layer.objects))):
        if not is_class(o): continue
        o.properties["fill_colour"] = color

def import_file(fn, diagram_data):
    import_javadoc(load_xml(fn), diagram_data.active_layer)

def load_file(fn):
    import_file(fn, dia.active_display().diagram.data)

dia.register_import("xmldoclet", "xml", import_file)
