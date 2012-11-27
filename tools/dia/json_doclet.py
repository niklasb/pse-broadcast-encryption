# import json-doclet's output as UML classes
import json
import dia

visibility = {'public':0, 'private':1, 'protected':2};
inheritance = {'pure-virtual':0, 'virtual':1, 'non-virtual':2}

def parse_html_color(s):
    return tuple(int(s[i:i+2], 16)/255.0 for i in (1,3,5))

default_color = parse_html_color("#FFDEC2")

def transform_field(**field):
    return (field["name"],
            field["type"],
            "",  # value
            field["comment-text"],
            visibility[field["visibility"]],
            0,
            field["static"],
            )

def transform_param(**param):
    return (param["name"], param["simple-type"], None, param.get("comment", ""), 0)

def transform_method(meth, abstract=False):
    return (meth["name"],
            meth["return-type"],
            meth["comment-text"],
            "",  # stereotype
            visibility[meth["visibility"]],
            inheritance["pure-virtual" if abstract or meth["abstract"] else "non-virtual"],
            meth["final"],
            meth["static"],
            [transform_param(name=k, **v) for k,v in meth["parameters"].items()],
            )

def fix_kerning(s):
    return u"\u200B".join(s)

def add_class(cls, layer):
    o, _, _ = dia.get_object_type("UML - Class").create(0,0)
    o.properties["name"] = fix_kerning(cls["name"])
    o.properties["abstract"] = cls["abstract"]
    o.properties["attributes"] = [transform_field(name=k, **v)
                                  for k, v in cls["fields"].items()]
    o.properties["operations"] = [transform_method(meth, cls["interface"])
                                  for meth in cls["methods"].values()]
    o.properties["fill_colour"] = default_color
    layer.add_object(o)
    return o

def add_extends(child, parent, layer):
    con, h1, h2 = dia.get_object_type("UML - Generalization").create(0,0)
    layer.add_object(con)
    h1.connect(parent.connections[6])
    h2.connect(child.connections[1])

def import_javadoc(obj, layer):
    classes = [dict(name=name, **cls) for pkg in obj['packages'].values()
                                      for name,cls in pkg['classes'].items()]
    objects = { cls["qualified-name"] : (cls, add_class(cls, layer))
                for cls in classes }
    for cls, obj in objects.values():
        bases = [x for x in [cls.get('superclass')] + cls['interfaces'] if x]
        for base in bases:
            if base not in objects: continue
            _, parent = objects[base]
            add_extends(obj, parent, layer)
    layer.update_extents()

def load_json(fn):
    with open(fn, "r") as f:
        return json.load(f)

def is_class(obj):
    return "UML - Class" == str(obj.type)

def change_class_colors(html_col, layer=None):
    layer = layer or dia.active_display().diagram.data.active_layer
    color = parse_html_color(html_col)
    for o in (layer.objects[i] for i in xrange(len(layer.objects))):
        if not is_class(o): continue
        o.properties["fill_colour"] = color

def import_file(fn, diagram_data):
    import_javadoc(load_json(fn), diagram_data.active_layer)

def load_file(fn):
    import_file(fn, dia.active_display().diagram.data)

dia.register_import("JavaDoc JSON", "json", import_file)
