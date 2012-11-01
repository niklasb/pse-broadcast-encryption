make:
	iconv -f utf-8 -t latin1 usecase.mp > usecase.latin1.mp
	mpost usecase.latin1.mp
	pdflatex pflichtenheft.tex -output-format=pdf
	makeglossaries pflichtenheft
	pdflatex pflichtenheft.tex -output-format=pdf
	make clean

clean:
	rm -rf  $(TARGET) *.class *.html *.log *.aux *.out *.glo *.glg *.gls *.ist *.xdy *.1 *.toc
