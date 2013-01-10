#include <iostream>

#include "LagrangeInterpolation.h"
#include "bigint_conversions.h"

void compute_coefficients(int n, mpz_t xs[], mpz_t cs[], mpz_t mod) {
  mpz_t z, diff;
  mpz_init(z);
  mpz_init(diff);

  mpz_set_ui(z, 1);
  for (int i = 0; i < n; ++i) {
    mpz_mul(z, z, xs[i]);
    mpz_mod(z, z, mod);
  }

  for (int i = 0; i < n; ++i) {
    mpz_set(cs[i], xs[i]);
    for (int j = 0; j < n; ++j) {
      if (i == j) continue;
      mpz_sub(diff, xs[j], xs[i]);
      mpz_mul(cs[i], cs[i], diff);
      mpz_mod(cs[i], cs[i], mod);
    }
    mpz_invert(cs[i], cs[i], mod);
    mpz_mul(cs[i], cs[i], z);
    mpz_mod(cs[i], cs[i], mod);
  }

  mpz_clear(diff);
  mpz_clear(z);
}

extern "C"
JNIEXPORT jobjectArray JNICALL Java_cryptocast_crypto_LagrangeInterpolation_nativeComputeCoefficients
  (JNIEnv * env, jclass cls, jobjectArray jxs, jbyteArray jmod)
{
  jclass byteArrayClass = env->GetObjectClass(jmod);

  mpz_t mmod;
  mpz_init(mmod);

  convert_j2mp(env, jmod, mmod);
  jsize n = env->GetArrayLength(jxs);
  mpz_t *mxs = new mpz_t[n],
        *mcs = new mpz_t[n];
  for (int i = 0; i < n; ++i) {
    mpz_init(mxs[i]);
    mpz_init(mcs[i]);
    convert_j2mp(env, (jbyteArray)env->GetObjectArrayElement(jxs, i), mxs[i]);
  }

  compute_coefficients(n, mxs, mcs, mmod);

  jobjectArray jcs = env->NewObjectArray(n, byteArrayClass, NULL);
  jbyteArray jc;
  for (int i = 0; i < n; ++i) {
    convert_mp2j(env, mcs[i], &jc);
    env->SetObjectArrayElement(jcs, i, jc);
  }

  for (int i = 0; i < n; ++i) {
    mpz_clear(mxs[i]);
    mpz_clear(mcs[i]);
  }
  mpz_clear(mmod);
  delete[] mxs;
  delete[] mcs;

  return jcs;
}
