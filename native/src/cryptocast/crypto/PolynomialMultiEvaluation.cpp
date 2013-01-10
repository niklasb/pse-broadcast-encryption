#include <iostream>
#include <thread>

#include <flint/flint.h>
#include <flint/fmpz_mod_poly.h>

#include "PolynomialMultiEvaluation.h"
#include "bigint_conversions.h"

using namespace std;

struct subproduct_tree {
 public:
  subproduct_tree(fmpz points[], int n, fmpz_t mod) {
    this->n = n;
    fmpz_init(this->mod);
    fmpz_set(this->mod, mod);
    polys = new fmpz_mod_poly_struct[2*n - 1];
    fmpz_init(zero);
    fmpz_set_ui(zero, 0);
    fmpz_t neg;
    fmpz_init(neg);
    for (int i = n - 1; i < 2*n - 1; ++i) {
      fmpz_mod_poly_init(&polys[i], mod);
      fmpz_mod_poly_set_coeff_ui(&polys[i], 1, 1);
      fmpz_set(neg, zero);
      fmpz_sub(neg, neg, &points[i - n + 1]);
      fmpz_mod(neg, neg, mod);
      fmpz_mod_poly_set_coeff_fmpz(&polys[i], 0, neg);
    }
    for (int i = n - 2; i >= 0; --i) {
      fmpz_mod_poly_init(&polys[i], mod);
      fmpz_mod_poly_mul(&polys[i], &polys[left(i)], &polys[right(i)]);
    }
  }

  ~subproduct_tree() {
    delete[] polys;
  }

  void evaluate(fmpz_mod_poly_t poly, fmpz ys[]) {
    evaluate(poly, ys, 0);
  }

 private:
  void evaluate(fmpz_mod_poly_t poly, fmpz ys[], int i) {
    if (i >= n - 1) {
      fmpz_mod_poly_evaluate_fmpz(&ys[i - n + 1], poly, zero);
      return;
    }
    fmpz_mod_poly_t r0, r1;
    fmpz_mod_poly_init(r0, mod);
    fmpz_mod_poly_init(r1, mod);
    fmpz_mod_poly_rem(r0, poly, &polys[left(i)]);
    fmpz_mod_poly_rem(r1, poly, &polys[right(i)]);
    evaluate(r0, ys, left(i));
    evaluate(r1, ys, right(i));
    fmpz_mod_poly_clear(r0);
    fmpz_mod_poly_clear(r1);
  }

  static int left(int i) { return 2*i + 1; }
  static int right(int i) { return 2*i + 2; }

  int n;
  fmpz_mod_poly_struct *polys;
  fmpz_t mod, zero;
};

int next_power_of_two(int x) {
  int n = 1;
  while (n < x)
    n <<= 1;
  return n;
}

extern "C"
JNIEXPORT jobjectArray JNICALL
Java_cryptocast_crypto_PolynomialMultiEvaluation_nativeMultiEval
    (JNIEnv * env, jclass cls, jobjectArray jxs, jobjectArray jcoeffs, jbyteArray jmod,
     jint num_threads, jint chunk_size)
{
  if (next_power_of_two(num_threads) != num_threads ||
      next_power_of_two(chunk_size) != chunk_size) {
    env->ThrowNew(env->FindClass("java/lang/IllegalArgumentException"),
                  "numThreads and chunkSize must be powers of two");
    return NULL;
  }
  jclass byteArrayClass = env->GetObjectClass(jmod);

  fmpz_t fmod;
  convert_j2fmp(env, jmod, fmod);

  jsize len_xs = env->GetArrayLength(jxs),
        len_coeffs = env->GetArrayLength(jcoeffs);

  jobjectArray jys = env->NewObjectArray(len_xs, byteArrayClass, NULL);

  fmpz_mod_poly_t fpoly;
  fmpz_mod_poly_init2(fpoly, fmod, len_coeffs);

  fmpz_t fcoeff;
  fmpz_init(fcoeff);
  for (int i = 0; i < len_coeffs; ++i) {
    convert_j2fmp(env, (jbyteArray)env->GetObjectArrayElement(jcoeffs, i),
                  fcoeff);
    fmpz_mod_poly_set_coeff_fmpz(fpoly, i, fcoeff);
  }

  int n = next_power_of_two(len_xs);

  fmpz *fxs = new fmpz[n],
       *fys = new fmpz[n];
  for (int i = 0; i < n; ++i) {
    fmpz_init(&fxs[i]);
    fmpz_init(&fys[i]);
  }

  int i = 0;
  for (; i < len_xs; ++i) {
    convert_j2fmp(env, (jbyteArray)env->GetObjectArrayElement(jxs, i),
                  &fxs[i]);
  }
  for (; i < n; ++i) {
    fmpz_set_ui(&fxs[i], 0);
  }

  if (n < chunk_size) {
    subproduct_tree tree(fxs, n, fmod);
    tree.evaluate(fpoly, fys);
  } else {
    thread *threads[num_threads];
    for (int i = 0; i < num_threads; ++i) {
      threads[i] = new thread([=,&fxs,&fys,&fpoly,&fmod] {
        int l = i * (n / num_threads);
        int r = (i + 1) * (n / num_threads);
        for (int j = l; j < r; j += chunk_size) {
          subproduct_tree tree(fxs + j, chunk_size, fmod);
          tree.evaluate(fpoly, fys + j);
        }
      });
    }
    for (int i = 0; i < num_threads; ++i) {
      threads[i]->join();
      delete threads[i];
    }
  }

  jbyteArray jy;
  for (int i = 0; i < len_xs; ++i) {
    convert_fmp2j(env, &fys[i], &jy);
    env->SetObjectArrayElement(jys, i, jy);
  }

  for (int i = 0; i < n; ++i) {
    fmpz_clear(&fxs[i]);
    fmpz_clear(&fys[i]);
  }
  delete[] fxs;
  delete[] fys;
  fmpz_clear(fmod);
  fmpz_mod_poly_clear(fpoly);

  return jys;
}
