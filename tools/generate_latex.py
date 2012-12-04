from textwrap import dedent
import xml.etree.ElementTree as ET
import sys

def class_name(cls):
    name = cls.find("name").text
    typevars = cls.find("typeVariables")
    if typevars is not None:
        name += "<%s>" % ", ".join(x.text for x in typevars.iter("name"))
    return name

def text_or(el, default=""):
    return el.text if el != None and el.text != None else default

def render_method(meth):
    return dedent("""\
        \lstinline|{attr}{ret}{name}({params})| \\\\
        {description}
        """).format(attr="",
                    ret="",
                    name=meth.find("name").text,
                    params="",
                    description=text_or(meth.find("comment")))

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
                                        for t in typevars.iter("com.ownedthx.xmldoclet.xmlbindings.TypeVar")))
    constructortxt = methodtxt = ""
    constructorsel = cls.find("constructors")
    if constructorsel is not None:
        constructortxt = dedent("""\
            \\textbf{{Constructors}}
            \\begin{{itemize}}
            {items}
            \\end{{itemize}}
            """).format(items="\n".join("\\item %s" % render_method(m) for m in constructorsel.iter("constructor")))
    methodsel = cls.find("methods")
    if methodsel is not None:
        methodtxt = dedent("""\
            \\textbf{{Methods}}
            \\begin{{itemize}}
            {items}
            \\end{{itemize}}
            """).format(items="\n".join("\\item %s" % render_method(m) for m in methodsel.iter("method")))
    return dedent("""\
           \\{sub}section{{{classtype} \\lstinline|{classname}|}}
           {description}
           {typevars}
           {constructortxt}
           {methodtxt}
           """).format(classtype=("Interface" if interface else "Class"),
                       classname=class_name(cls),
                       description=cls.find("comment").text,
                       typevars=typevarstxt,
                       constructortxt=constructortxt,
                       methodtxt=methodtxt,
                       sub="sub"*indent)

def render_package(pkg, indent=1):
    classes = list(pkg.iter("class")) + list(pkg.iter("interface"))
    return dedent("""\
           \\{sub}section{{Package {pkgname}}}
           {classes}
           """).format(pkgname=pkg.find("name").text,
                      classes="".join(render_class(cls, indent+1)
                                        for cls in classes),
                      sub="sub"*indent)

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print >>sys.stderr, "Usage: %s /path/to/javadoc.xml outdir" % sys.argv[0]
        sys.exit(1)
    doc = ET.parse(sys.argv[1]).getroot()
    outdir = sys.argv[2]
    with open(outdir + "/docs.tex", "w") as f:
        for p in doc.iter("package"):
            f.write(render_package(p))
