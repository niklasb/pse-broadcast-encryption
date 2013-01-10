#ifndef _BIGINT_CONVERSIONS_H
#define _BIGINT_CONVERSIONS_H

#include <jni.h>
#include <flint/flint.h>
#include <flint/fmpz.h>

void convert_j2mp(JNIEnv* env, jbyteArray jvalue, mpz_t mvalue);
void convert_mp2j(JNIEnv* env, mpz_t mvalue, jbyteArray* jvalue);
void convert_j2fmp(JNIEnv* env, jbyteArray jvalue, fmpz_t mvalue);
void convert_fmp2j(JNIEnv* env, fmpz_t mvalue, jbyteArray* jvalue);

#endif
