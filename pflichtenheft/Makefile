make:
	pdflatex pflichtenheft.tex -output-format=pdf
	makeglossaries pflichtenheft
	pdflatex pflichtenheft.tex -output-format=pdf
	make clean

clean:
	rm -rf  $(TARGET) *.class *.html *.log *.aux *.out *.glo *.glg *.gls *.ist *.xdy *.1 *.toc
