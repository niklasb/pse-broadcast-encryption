#include <iostream>
#include <thread>

#include "LagrangeInterpolation.h"
#include "bigint_conversions.h"

using namespace std;

void compute_chunk(int n, mpz_t xs[], mpz_t cs[], mpz_t mod,
                   mpz_t z, int l, int r) {
  mpz_t diff;
  mpz_init(diff);
  for (int i = l; i < r; i++) {
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
}

void compute_coefficients(int n, mpz_t xs[], mpz_t cs[], mpz_t mod,
                          int num_threads) {
  mpz_t z;
  mpz_init(z);

  mpz_set_ui(z, 1);
  for (int i = 0; i < n; ++i) {
    mpz_mul(z, z, xs[i]);
    mpz_mod(z, z, mod);
  }

  if (num_threads > 1) {
    int chunk_size = (n + num_threads - 1) / num_threads;
    thread *threads[num_threads];
    for (int t = 0; t < num_threads; ++t) {
      threads[t] = new thread([=,&z,&xs,&mod,&cs] {
        int l = t * chunk_size;
        int r = min(n, (t + 1) * chunk_size);
        if (r > l)
          compute_chunk(n, xs, cs, mod, z, l, r);
      });
    }
    for (int t = 0; t < num_threads; ++t) {
      threads[t]->join();
      delete threads[t];
    }
  } else {
    compute_chunk(n, xs, cs, mod, z, 0, n);
  }

  mpz_clear(z);
}

extern "C"
JNIEXPORT jobjectArray JNICALL Java_cryptocast_crypto_LagrangeInterpolation_nativeComputeCoefficients
  (JNIEnv * env, jclass cls, jobjectArray jxs, jbyteArray jmod, jint num_threads)
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

  compute_coefficients(n, mxs, mcs, mmod, num_threads);

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
