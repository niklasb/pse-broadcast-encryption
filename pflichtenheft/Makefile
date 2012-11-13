make:
	pdflatex pflichtenheft.tex
	makeglossaries pflichtenheft
	bibtex pflichtenheft
	pdflatex pflichtenheft.tex
	pdflatex pflichtenheft.tex
	make clean

clean:
	rm -rf *.class *.html *.log *.aux *.out *.glo *.glg *.gls *.ist *.xdy *.1 *.toc *.bbl *.blg
