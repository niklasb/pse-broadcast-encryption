#include <mpir.h>
#include <sys/time.h>
#include <unistd.h>

#include <cstdlib>
#include <iostream>
#include <thread>

#include "flint/flint.h"
#include "flint/fmpz_mod_poly.h"

using namespace std;

long timeInMs() {
  struct timeval t;
  gettimeofday(&t, 0);
  return t.tv_sec * 1000 + t.tv_usec / 1000;
}

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

  //~subproduct_tree() {
    //delete[] polys;
  //}

  void evaluate(fmpz ys[], fmpz_mod_poly_t poly) {
    evaluate(ys, poly, 0);
  }

 private:
  void evaluate(fmpz ys[], fmpz_mod_poly_t poly, int i) {
    if (i >= n - 1) {
      fmpz_mod_poly_evaluate_fmpz(&ys[i - n + 1], poly, zero);
      return;
    }
    fmpz_mod_poly_t r0, r1;
    fmpz_mod_poly_init(r0, mod);
    fmpz_mod_poly_init(r1, mod);
    fmpz_mod_poly_rem(r0, poly, &polys[left(i)]);
    fmpz_mod_poly_rem(r1, poly, &polys[right(i)]);
    evaluate(ys, r0, left(i));
    evaluate(ys, r1, right(i));
    fmpz_mod_poly_clear(r0);
    fmpz_mod_poly_clear(r1);
  }

  static int left(int i) { return 2*i + 1; }
  static int right(int i) { return 2*i + 2; }

  int n;
  fmpz_mod_poly_struct *polys;
  fmpz_t mod, zero;
};

//const int t = 5, n = 8;
const int t = 1000, n = 1<<17;

const int num_threads = 2, chunk_size = 1024;

int main(int argc, char* argv[]) {
  flint_rand_t rnd;
  flint_randinit(rnd);

  cout << "Preparing benchmark with t=" << t << " n=" << n << endl;
  fmpz_t q;
  fmpz_init(q);
  fmpz_set_str(q, "F518AA8781A8DF278ABA4E7D64B7CB9D49462353", 16);
  //fmpz_set_ui(q, 11);

  fmpz_mod_poly_t x;
  fmpz_mod_poly_init(x, q);
  fmpz_mod_poly_randtest(x, rnd, t);
  //fmpz_mod_poly_print(x); printf("\n");

  static fmpz xs[n], ys[n];
  for (int i = 0; i < n; ++i) {
    fmpz_init(&xs[i]);
    fmpz_init(&ys[i]);
    fmpz_randm(&xs[i], rnd, q);
  }
  //for (int i = 0; i < n; ++i) { fmpz_print(&xs[i]); printf(" "); } printf("\n");

  cout << "Running..." << endl;
  long t1 = timeInMs();

  //for (int i = 0; i < num_chunks; ++i) {
    //subproduct_tree tree1(xs + i * n/num_chunks, n/num_chunks, q);
    //tree1.evaluate(ys + i * n/num_chunks, x);
  //}

  //fmpz_t res; fmpz_init(res);
  //for (int i = 0; i < n; ++i) {
    //fmpz_mod_poly_evaluate_fmpz(res, x, &xs[i]);
  //}

  thread *threads[num_threads];
  for (int i = 0; i < num_threads; ++i) {
    threads[i] = new thread([=,&xs,&ys,&x,&q] {
      int l = i * (n / num_threads);
      int r = (i + 1) * (n / num_threads);
      for (int j = l; j < r; j += chunk_size) {
        subproduct_tree tree(xs + j, chunk_size, q);
        tree.evaluate(ys + j, x);
      }
    });
  }
  for (int i = 0; i < num_threads; ++i)
    threads[i]->join();

  cout << "Time in ms: " << (timeInMs() - t1) << endl;
  //for (int i = 0; i < n; ++i) { fmpz_print(&ys[i]); printf(" "); } printf("\n");

  /*fmpz_mod_poly_clear(x);*/
  /*fmpz_clear(n);*/
  return EXIT_SUCCESS;
}

