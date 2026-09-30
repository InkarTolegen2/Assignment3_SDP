def box(x,y,w,h,title,members=(),stereo=None,fill="#f7f7f7",italic=False):
    s=f'<rect x="{x}" y="{y}" width="{w}" height="{h}" fill="{fill}" stroke="#333" stroke-width="1.5"/>'
    ty=y+22
    if stereo:
        s+=f'<text x="{x+w/2}" y="{ty}" text-anchor="middle" font-size="12" fill="#444">{stereo}</text>'; ty+=16
    st=' font-style="italic"' if italic else ''
    s+=f'<text x="{x+w/2}" y="{ty}" text-anchor="middle" font-size="15" font-weight="bold"{st}>{title}</text>'
    hy=ty+8
    s+=f'<line x1="{x}" y1="{hy}" x2="{x+w}" y2="{hy}" stroke="#333"/>'
    yy=hy+18
    for m in members:
        if m=="--":
            s+=f'<line x1="{x}" y1="{yy-12}" x2="{x+w}" y2="{yy-12}" stroke="#333"/>'; yy+=6; continue
        s+=f'<text x="{x+8}" y="{yy}" font-size="12" font-family="monospace">{m}</text>'; yy+=17
    return s
def line(x1,y1,x2,y2,dash=False,end=None,start=None):
    d=' stroke-dasharray="6,4"' if dash else ''
    me=f' marker-end="url(#{end})"' if end else ''
    ms=f' marker-start="url(#{start})"' if start else ''
    return f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="#333" stroke-width="1.5"{d}{me}{ms}/>'
def txt(x,y,t,size=12,anchor="middle",color="#222",style=""):
    return f'<text x="{x}" y="{y}" text-anchor="{anchor}" font-size="{size}" fill="{color}" {style}>{t}</text>'

o=[]
o.append('<svg xmlns="http://www.w3.org/2000/svg" width="1260" height="700" viewBox="0 0 1260 700" font-family="Arial, Helvetica, sans-serif">')
o.append('''<defs>
<marker id="tri" markerWidth="14" markerHeight="14" refX="12" refY="7" orient="auto"><path d="M1,1 L12,7 L1,13 Z" fill="#fff" stroke="#333" stroke-width="1.5"/></marker>
<marker id="dia" markerWidth="16" markerHeight="12" refX="1" refY="6" orient="auto"><path d="M1,6 L8,1 L15,6 L8,11 Z" fill="#fff" stroke="#333" stroke-width="1.5"/></marker>
<marker id="open" markerWidth="12" markerHeight="12" refX="11" refY="6" orient="auto"><path d="M1,1 L11,6 L1,11" fill="none" stroke="#333" stroke-width="1.5"/></marker>
</defs>''')
o.append('<rect width="1260" height="700" fill="#fff"/>')
o.append(txt(630,28,"Lab Label Printing - Bridge + Adapter (UML class diagram)",18,style='font-weight="bold"'))
# regions
o.append('<rect x="15" y="45" width="440" height="330" fill="#eef5ff" stroke="#9bb8e6" stroke-dasharray="4,4"/>')
o.append(txt(235,63,"ABSTRACTION hierarchy",12,color="#37588f"))
o.append('<rect x="470" y="45" width="770" height="330" fill="#effaf0" stroke="#9bd6a4" stroke-dasharray="4,4"/>')
o.append(txt(855,63,"IMPLEMENTOR hierarchy",12,color="#2d6b3a"))

# Abstraction
o.append(box(40,90,380,150,"Label",["- printer: LabelPrinter","--","# render(): RenderedLabel  {abstract}","+ print(copies: int): PrintReceipt","+ printOne(): PrintReceipt"],italic=True,fill="#dbe9ff"))
o.append(box(25,290,200,58,"SpecimenLabel",["# render(): RenderedLabel"],fill="#dbe9ff"))
o.append(box(245,290,200,58,"ReagentLabel",["# render(): RenderedLabel"],fill="#dbe9ff"))
o.append(line(125,290,200,240,end="tri"))
o.append(line(345,290,265,240,end="tri"))

# Implementor
o.append(box(520,90,360,80,"LabelPrinter",["+ print(RenderedLabel, int): PrintReceipt"],stereo="&#171;interface&#187;",fill="#d8f3dc"))
o.append(box(490,260,200,58,"ThermalZplPrinter",["+ print(...)"],fill="#d8f3dc"))
o.append(box(710,260,200,58,"FileLabelPrinter",["+ print(...)"],fill="#d8f3dc"))
o.append(box(930,260,290,58,"DotMatrixPrinterAdapter",["+ print(...)  // ADAPTER"],fill="#ffe8b3"))
o.append(line(590,260,620,170,dash=True,end="tri"))
o.append(line(810,260,740,170,dash=True,end="tri"))
o.append(line(1075,260,840,170,dash=True,end="tri"))

# the bridge
o.append(line(420,130,520,130,end=None,start="dia"))
o.append(txt(470,120,"printer",12,style='font-style="italic"'))
o.append(txt(470,150,"(the BRIDGE)",11,color="#a00"))

# Resolver
o.append(box(940,90,290,80,"PrinterResolver",["+ register(scheme, factory)","+ resolve(destination): LabelPrinter"],fill="#f7f7f7"))
o.append(line(940,130,880,130,dash=True,end="open"))
o.append(txt(910,120,"&#171;creates&#187;",11))

# adaptee
o.append(box(930,420,290,120,"DotMatrixDriver",["+ open() throws DotMatrixLinkException","+ emit(short, byte[]): int","+ close()","codes: 0, -1, -11, -23, -40"],stereo="&#171;third-party, unmodified&#187;",fill="#ffe8b3"))
o.append(line(1075,318,1075,420,end="open"))
o.append(txt(1130,375,"adapts (has-a)",11,anchor="middle"))

# exception
o.append(box(490,420,380,120,"PrintFailedException",["extends RuntimeException","Reason: DEVICE_UNAVAILABLE, OUT_OF_MEDIA,","        INVALID_LABEL, UNSUPPORTED_DESTINATION,","        UNKNOWN"],fill="#fde2e2"))
o.append(line(590,318,590,420,dash=True,end="open"))
o.append(line(810,318,810,420,dash=True,end="open"))
o.append(line(960,318,870,455,dash=True,end="open"))
o.append(txt(700,395,"&#171;throws&#187; (only failure type of the contract)",11,color="#a00"))

# value objects
o.append(box(40,420,380,90,"Value objects (records)",["RenderedLabel(title, lines, barcode)","PrintReceipt(copiesPrinted, deviceName)"],stereo="&#171;data&#187;"))

# footer
o.append(txt(30,600,"Client (Main): printer = resolver.resolve(destination);  new SpecimenLabel(printer, ...).print(2)  - the client never names a concrete printer class.",12,anchor="start"))
o.append(txt(30,625,"Bridge: Label (+ SpecimenLabel, ReagentLabel) varies independently of LabelPrinter (+ Thermal, File, DotMatrixAdapter).",12,anchor="start"))
o.append(txt(30,650,"Adapter: DotMatrixPrinterAdapter converts label + int copies -> ASCII byte[] + short, and status codes / checked exception -> PrintFailedException(Reason).",12,anchor="start"))
o.append('</svg>')
open("uml-class-diagram.svg","w",encoding="utf-8").write("\n".join(o))
