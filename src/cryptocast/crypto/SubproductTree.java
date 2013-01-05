package cryptocast.crypto;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class SubproductTree<T> {
    private int n;
    private ImmutableList<T> us;
    private ImmutableList<Polynomial<T>> tree;
    private Field<T> field;
    
    private SubproductTree(Field<T> field, List<T> us, List<Polynomial<T>> tree) {
        this.field = field;
        this.us = ImmutableList.copyOf(us);
        n = us.size();
        assert Integer.bitCount(n) == 1;
        this.tree = ImmutableList.copyOf(tree);
    }
    
    public Polynomial<T> getRoot() {
        return tree.get(0);
    }
    
    public Field<T> getField() {
        return field;
    }
    
    public ImmutableList<T> evaluate(Polynomial<T> poly) {
        Preconditions.checkArgument(poly.getDegree() < n, 
                "Polynomial must have degree < n (n is %d, but degree is %d)", 
                n, poly.getDegree());
        return evaluate(0, poly);
    }
    
    private ImmutableList<T> evaluate(int i, Polynomial<T> poly) {
        assert field.equals(poly.getField());
        assert poly.getDegree() < us.size();
        if (i >= n - 1) {
            assert poly.getDegree() <= 0;
            return ImmutableList.of(poly.getDegree() == 0 ? poly.getCoefficient(0) : field.zero());
        }
        Polynomial<T> r0 = poly.mod(tree.get(left(i))),
                      r1 = poly.mod(tree.get(right(i)));
        ImmutableList<T> l = evaluate(left(i), r0),
                         r = evaluate(right(i), r1);
        return ImmutableList.<T>builder().addAll(l).addAll(r).build();
    }
    
    public static <T> SubproductTree<T> buildPow2(Field<T> field, List<T> us) {
        int n = us.size();
        int k = 1;
        while (n > (1 << k)) {
            k++;
        }
        assert n == (1 << k);
        List<Polynomial<T>> polys = Lists.newArrayListWithCapacity(2*n - 1);
        for (int i = 0; i < 2*n - 1; ++i) {
            polys.add(i >= n - 1 ? makeAtomicPolynomial(field, us.get(i - n + 1)) : null);
        }
        for (int i = n - 2; i >= 0; --i) {
            polys.set(i, polys.get(left(i)).multiply(polys.get(right(i))));
        }
        return new SubproductTree<T>(field, us, polys);
    }
    
    // create the polynomial P(x) = x - u
    private static <T> Polynomial<T> makeAtomicPolynomial(Field<T> field, T u) {
        return new Polynomial<T>(field, ImmutableList.<T>of(field.negate(u), field.one()));
    }
    
    private static int left(int i) { return 2*i + 1; }
    private static int right(int i) { return 2*i + 2; }
}
