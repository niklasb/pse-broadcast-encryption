from textwrap import dedent
import xml.etree.ElementTree as ET
import sys
import re

boilerplate_classes = ['java.lang.Object']

def escape_latex(txt):
    return txt.replace("_", "\\_")

def class_name(cls):
    name = cls.find("name").text
    typevars = cls.find("typeVariables")
    if typevars is not None:
        name += "<%s>" % ", ".join(x.text for x in typevars.iter("name"))
    return name

def text_or(el, default=""):
    return el.text if el != None and el.text != None else default

def parse_bool(s, default=False):
    return s.strip() == 'true'

def transform_type(typ, simple=True):
    if typ.tag == "string": return typ.text
    dim = typ.find("dimension").text or ""
    gen = ""
    typevars = typ.find("generics")
    if typevars is not None: typevars = typevars.find("typeArguments")
    if typevars is not None:
        gen = "<%s>" % ", ".join(transform_type(t) for t in typevars.iter("type"))
    res = typ.find("qualifiedName").text + gen + dim
    return res.split(".")[-1] if simple else res

def replace_code(txt):
    txt = re.sub(r"{@link\s+([^}]*)}", r"\lstinline|\1|", txt)
    txt = re.sub(r"<code>([^<]*)</code>", r"\lstinline|\1|", txt)
    return txt

def render_param(param, varargs=False):
    dim = "..." if varargs else ""
    return "%s %s%s" % (transform_type(param.find("type")), param.find("name").text, dim)

def render_method(meth):
    rettype = ""
    res = meth.find("result")
    if res != None:
        rettype = " " + transform_type(res.find("type"))
    attr = "%s%s%s" % (
            meth.find("scope").text,
            " abstract" if parse_bool(text_or(meth.find("isAbstract"), "false")) else "",
            " static" if parse_bool(text_or(meth.find("isStatic"), "false")) else "",
            )
    params = list(meth.iter("parameter"))
    varargs = parse_bool(meth.find("isVarArgs").text)
    paramtxt = ""
    paramscom = [p for p in params if p.find("comment") != None and p.find("comment").text != None]
    if paramscom:
        paramtxt = dedent("""\
            \\begin{{itemize}}
            {items}
            \\end{{itemize}}
            """).format(items="\n".join("\\item \\lstinline|%s|: %s" % (
                                            p.find("name").text, p.find("comment").text)
                                        for p in paramscom))
    returntxt = ""
    if res != None and res.find("comment") != None:
        returntxt = "\emph{Returns:} %s" % (res.find("comment").text or "").replace("#", "\\#")

    description = text_or(meth.find("comment"))
    if not description.strip() and returntxt.strip():
        description = returntxt
        returntxt = ""

    return dedent("""\
        \\lstinline|{attr}{rettype}| \\lstinline|{name}|\\lstinline|({params})|{sep} \\\\[-0.6em]
        {description}
        {paramtxt}
        {returntxt}
        """).format(attr=attr,
                    rettype=rettype,
                    name=meth.find("name").text,
                    params=", ".join(render_param(params[i],
                                                  varargs and i == len(params)-1)
                                     for i in xrange(len(params))),
                    paramtxt=paramtxt,
                    returntxt=returntxt,
                    description=description,
                    sep="\\\\" if description.strip() else "",
                    )

def render_tikz_param(param, varargs=False):
    dim = "..." if varargs else ""
    return "%s : %s%s" % (escape_latex(param.find("name").text),
                          transform_type(param.find("type")), dim)

def render_tikz_method(meth, abstract=False):
    varargs = parse_bool(meth.find("isVarArgs").text)
    rettype = ""
    res = meth.find("result")
    if res != None:
        typ = transform_type(res.find("type"))
        if typ != "void":
            rettype = " : " + typ
    access = {"public":"+",
              "protected":"\\#",
              "private":"--",
              "packageprivate":"\\#"}[meth.find("scope").text]
    params = list(meth.iter("parameter"))
    paramstxt = ", ".join(render_tikz_param(params[i], varargs and i == len(params)-1)
                          for i in xrange(len(params)))
    txt = "%s %s(%s)%s" % (access, meth.find("name").text, paramstxt, rettype)
    if parse_bool(meth.find("isStatic").text):
        txt = "\\umlstatic{%s}"%txt
    if parse_bool(meth.find("isAbstract").text) or abstract:
        txt = "\\umlvirt{%s}"%txt
    return txt

def render_class_diagram(cls):
    abstr = cls.find("isAbstract")
    interface = abstr is None # if this isn't present, cls is an interface
    isAbstr = parse_bool(text_or(abstr, "true"))
    methodsel = cls.find("methods")
    methods = ""
    if methodsel is not None:
        pubmethods = filter(lambda m: m.find("scope").text == "public", methodsel.iter("method"))
        methods = " \\\\\n".join(render_tikz_method(m, interface) for m in pubmethods)
    return dedent("""\
        \\noindent\\begin{{minipage}}[t]{{5cm}}
        \\vspace{{0.3em}}
        \\hspace*{{2em}}
        \\begin{{tikzpicture}}
        \\umlclass[{classparams}]{{{classname}}}{{
        {fields}
        }}{{
        {methods}
        }}
        \\end{{tikzpicture}}
        \\vspace{{0.3em}}
        \\end{{minipage}}
        """).format(classname=class_name(cls),
                    classparams=("type=abstract" if isAbstr else ""),
                    fields="",
                    methods=methods,
                    )

def captionize(txt):
    return "\\textbf{\\sffamily %s}" % txt

def render_class(cls, indent=2):
    abstr = cls.find("isAbstract")
    interface = abstr is None # if this isn't present, cls is an interface
    typevarstxt = ""
    typevars = cls.find("typeVariables")
    if typevars is not None:
        typevarstxt = dedent("""\
            \\begin{{itemize}}
            {items}
            \\end{{itemize}}
            """).format(items="\n".join("\\item \\lstinline|<%s>|: %s" % (
                                            t.find("name").text, text_or(t.find("comment")))
                                        for t in typevars.iter("typeVar")))
    constructortxt = methodtxt = ""
    constructorsel = cls.find("constructors")
    bases = []
    supclass = cls.find("superClass")
    if supclass is not None: bases.append(transform_type(supclass, simple=False))
    ifaces = cls.find('interfaces')
    if ifaces is not None: bases += list(sorted(transform_type(i,simple=False) for i in ifaces))
    bases = [b for b in bases if b not in boilerplate_classes]
    basestxt = ""
    if bases:
        basestxt = dedent("""\

            {caption}
            \\begin{{itemize}}
            {items}
            \\end{{itemize}}
            """).format(caption=captionize("Superclasses and Interfaces"),
                        items="\n".join("\\item \\lstinline|%s|" % b for b in bases))

    if constructorsel is not None:
        constr = [m for m in constructorsel.iter("constructor") if not parse_bool(m.find("isDefault").text)]
        if constr:
            constructortxt = dedent("""\

            {caption}
            \\begin{{itemize}}
            {items}
            \\end{{itemize}}
            """).format(caption=captionize("Constructors"),
                        items="\n".join("\\item %s" % render_method(m) for m in constr))
    methodsel = cls.find("methods")
    if methodsel is not None:
        pubmethods = filter(lambda m: m.find("scope").text == "public", methodsel.iter("method"))
        if pubmethods:
            methodtxt = dedent("""\

                {caption}
                \\begin{{itemize}}
                {items}
                \\end{{itemize}}
                """).format(caption=captionize("Methods"),
                            items="\n".join("\\item %s" % render_method(m)
                                            for m in pubmethods))
    return dedent("""\
           \\{sub}section{{{classtype} \\lstinline|{classname}|}}
           {description} \\\\
           {diagram}
           {typevars}
           {basestxt}
           {constructortxt}
           {methodtxt}
           """).format(classtype=("Interface" if interface else "Class"),
                       diagram=render_class_diagram(cls),
                       classname=class_name(cls),
                       description=(cls.find("comment").text or "").replace("#", "\\#"),
                       typevars=typevarstxt,
                       constructortxt=constructortxt,
                       methodtxt=methodtxt,
                       basestxt=basestxt,
                       sub="sub"*indent)

def render_package(pkg, indent=1):
    classes = list(pkg.iter("class")) + list(pkg.iter("interface"))
    return replace_code(dedent("""\
           \\{sub}section{{Package \\lstinline!{pkgname}!}}
           {comment}
           \\input{{{pkgname}.custom.tex}}
           {classes}
           """).format(pkgname=pkg.find("name").text,
                      classes="".join(render_class(cls, indent+1)
                                        for cls in classes),
                      sub="sub"*indent,
                      comment=text_or(pkg.find("comment"))))

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print >>sys.stderr, "Usage: %s /path/to/javadoc.xml outdir" % sys.argv[0]
        sys.exit(1)
    doc = ET.parse(sys.argv[1]).getroot()
    outdir = sys.argv[2]
    for p in doc.iter("package"):
        name = p.find("name").text
        print "Processing package %s" % name
        with open(outdir + "/" + name + ".tex", "w") as f:
            f.write(render_package(p))
        with open(outdir + "/" + name + ".custom.tex", "a") as f:
            pass
