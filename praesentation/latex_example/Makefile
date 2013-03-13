DOC := KIT-Folien-de
all:
	make clean
	pdflatex $(DOC)
	bibtex $(DOC)
	pdflatex $(DOC)
	pdflatex $(DOC)

clean:
	-rm $(DOC).pdf $(DOC).out $(DOC).bbl $(DOC).log $(DOC).blg $(DOC).aux
