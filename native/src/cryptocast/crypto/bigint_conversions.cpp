#include "bigint_conversions.h"

void convert_j2mp(JNIEnv* env, jbyteArray jvalue, mpz_t mvalue)
{
  jsize size;
  jbyte* jbuffer;

  size = env->GetArrayLength(jvalue);
  jbuffer = env->GetByteArrayElements(jvalue, NULL);

  mpz_realloc2(mvalue, sizeof(jbyte) * 8 * size); //preallocate the size
  mpz_import(mvalue, size, 1, sizeof(jbyte), 1, 0, (void*)jbuffer);

  env->ReleaseByteArrayElements(jvalue, jbuffer, JNI_ABORT);
}

void convert_mp2j(JNIEnv* env, mpz_t mvalue, jbyteArray *jvalue)
{
  size_t size = (mpz_sizeinbase(mvalue, 2) + 7) / 8 + sizeof(jbyte);
  *jvalue = env->NewByteArray(size);

  jboolean copy = JNI_FALSE;
  jbyte* buffer = env->GetByteArrayElements(*jvalue, &copy);
  buffer[0] = 0x00;
  mpz_export((void*)&buffer[1], &size, 1, sizeof(jbyte), 1, 0, mvalue);

  env->ReleaseByteArrayElements(*jvalue, buffer, 0);
}

void convert_j2fmp(JNIEnv* env, jbyteArray jvalue, fmpz_t mvalue)
{
  mpz_t tmp;
  mpz_init(tmp);

  convert_j2mp(env, jvalue, tmp);
  fmpz_set_mpz(mvalue, tmp);

  mpz_clear(tmp);
}

void convert_fmp2j(JNIEnv* env, fmpz_t mvalue, jbyteArray* jvalue)
{
  mpz_t tmp;
  mpz_init(tmp);

  fmpz_get_mpz(tmp, mvalue);
  convert_mp2j(env, tmp, jvalue);

  mpz_clear(tmp);
}
