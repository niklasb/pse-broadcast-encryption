ROOT := ..
CLASSPATH := ${ROOT}/projects/server/bin:${ROOT}/3rd_party/guava-13.0.1.jar
JAVAH := javah -classpath ${CLASSPATH}
GPP := g++ -O3 -std=c++11 -lpthread

.PHONE: all clean

all: lib/libIntegersModuloPrime.so \
     lib/libPolynomialMultiEvaluation.so \
     lib/libLagrangeInterpolation.so

clean:
	rm -rf lib/*

headers:
	${JAVAH} -o src/cryptocast/crypto/PolynomialMultiEvaluation.h \
	  cryptocast.crypto.PolynomialMultiEvaluation
	${JAVAH} -o src/cryptocast/crypto/LagrangeInterpolation.h \
	  cryptocast.crypto.LagrangeInterpolation
	${JAVAH} -o src/cryptocast/crypto/IntegersModuloPrime.h \
	  cryptocast.crypto.IntegersModuloPrime

lib/libIntegersModuloPrime.so: src/cryptocast/crypto/IntegersModuloPrime.cpp \
                               src/cryptocast/crypto/IntegersModuloPrime.h
	mkdir -p lib
	${GPP} -fPIC -I${JDK_HOME}/include -I${JDK_HOME}/include/linux -shared \
	  -lgmp \
	  src/cryptocast/crypto/IntegersModuloPrime.cpp \
          src/cryptocast/crypto/bigint_conversions.cpp \
	  -o lib/libIntegersModuloPrime.so

lib/libLagrangeInterpolation.so: src/cryptocast/crypto/LagrangeInterpolation.cpp \
                               src/cryptocast/crypto/LagrangeInterpolation.h
	mkdir -p lib
	${GPP} -fPIC -I${JDK_HOME}/include -I${JDK_HOME}/include/linux -shared \
	  -lgmp \
	  src/cryptocast/crypto/LagrangeInterpolation.cpp \
          src/cryptocast/crypto/bigint_conversions.cpp \
	  -o lib/libLagrangeInterpolation.so

lib/libPolynomialMultiEvaluation.so: src/cryptocast/crypto/PolynomialMultiEvaluation.cpp \
                                     src/cryptocast/crypto/PolynomialMultiEvaluation.h
	mkdir -p lib
	${GPP} -fPIC -I${JDK_HOME}/include -I${JDK_HOME}/include/linux -shared \
	  -lgmp -lflint \
	  src/cryptocast/crypto/PolynomialMultiEvaluation.cpp \
          src/cryptocast/crypto/bigint_conversions.cpp \
	  -o lib/libPolynomialMultiEvaluation.so
