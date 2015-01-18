package swingDarts;

import java.math.BigDecimal;

public class SiftParameter2 {
	BigDecimal Positive = new BigDecimal("0");
	BigDecimal Negative = new BigDecimal("1");
	BigDecimal DeltaT = new BigDecimal("0.01");
	BigDecimal DeltaTinv = new BigDecimal("100.0");
	BigDecimal COUNTTHR = new BigDecimal("5");
	BigDecimal COUNTLIM = new BigDecimal("100");
	BigDecimal ROTTHR = DeltaTinv.multiply(new BigDecimal("20.0"));
	BigDecimal GUARD = new BigDecimal("2.0");
	
	BigDecimal i;
	BigDecimal t;
	BigDecimal ax, ay, az, gx, gy, gz, avrms;
	BigDecimal rx, ry, rz, rzp, gxp, gyp, gzp;
	BigDecimal px, py, pz, ctx, cty, ctz, peakt_gz, stz;
	
	public SiftParameter2() {
		t = new BigDecimal("0.00");
		i = new BigDecimal("0");
		ax = new BigDecimal("0.000");
		ay = new BigDecimal("0.000");
		az = new BigDecimal("0.000");
		
	}
}
