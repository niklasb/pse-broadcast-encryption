package cryptocast.crypto;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.math.ec.ECCurve;

public class NamedEllipticCurves {
    public static ECCurve prime192v1 = 
            ECNamedCurveTable.getParameterSpec("prime192v1").getCurve();
}
