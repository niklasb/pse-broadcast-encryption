#include <iostream>

#include "IntegersModuloPrime.h"
#include "bigint_conversions.h"

extern "C"
JNIEXPORT jbyteArray JNICALL Java_cryptocast_crypto_IntegersModuloPrime_nativeModPow
  (JNIEnv * env, jclass cls, jbyteArray jbase, jbyteArray jexp, jbyteArray jmod)
{
  mpz_t mbase;
  mpz_t mexp;
  mpz_t mmod;
  mpz_init(mbase);
  mpz_init(mexp);
  mpz_init(mmod);

  convert_j2mp(env, jbase, mbase);
  convert_j2mp(env, jexp, mexp);
  convert_j2mp(env, jmod, mmod);

  mpz_powm(mmod, mbase, mexp, mmod);

  jbyteArray jresult;
  convert_mp2j(env, mmod, &jresult);

  mpz_clear(mbase);
  mpz_clear(mexp);
  mpz_clear(mmod);

  return jresult;
}
