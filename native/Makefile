JAVAH := javah -classpath ../projects/server/bin
GPP := g++ -O3 -std=c++11 -lpthread

.PHONE: all clean

all: lib/libIntegersModuloPrime.so lib/libPolynomialMultiEvaluation.so

clean:
	rm -rf lib/*

headers:
	${JAVAH} -o src/cryptocast/crypto/PolynomialMultiEvaluation.h \
	  cryptocast.crypto.PolynomialMultiEvaluation
	${JAVAH} -o src/cryptocast/crypto/IntegersModuloPrime.h \
	  cryptocast.crypto.IntegersModuloPrime

lib/libIntegersModuloPrime.so: src/cryptocast/crypto/IntegersModuloPrime.cpp \
                               src/cryptocast/crypto/IntegersModuloPrime.h
	${GPP} -fPIC -I${JDK_HOME}/include -I${JDK_HOME}/include/linux -shared \
	  -lgmp \
	  src/cryptocast/crypto/IntegersModuloPrime.cpp \
          src/cryptocast/crypto/bigint_conversions.cpp \
	  -o lib/libIntegersModuloPrime.so

lib/libPolynomialMultiEvaluation.so: src/cryptocast/crypto/PolynomialMultiEvaluation.cpp \
                                     src/cryptocast/crypto/PolynomialMultiEvaluation.h
	${GPP} -fPIC -I${JDK_HOME}/include -I${JDK_HOME}/include/linux -shared \
	  -lgmp -lflint \
	  src/cryptocast/crypto/PolynomialMultiEvaluation.cpp \
          src/cryptocast/crypto/bigint_conversions.cpp \
	  -o lib/libPolynomialMultiEvaluation.so
