package swingDarts;

import java.math.BigDecimal;

public class SiftParameter {
	/*
	BigDecimal temp = new BigDecimal(20.0);
	
	BigDecimal Positive = new BigDecimal(0);
	BigDecimal Negative = new BigDecimal(1);
	BigDecimal DeltaT = new BigDecimal(0.01);
	BigDecimal DeltaTinv = new BigDecimal(100.0);
	BigDecimal COUNTTHR = new BigDecimal(5);
	BigDecimal COUNTLIM = new BigDecimal(100);
	BigDecimal ROTTHR = DeltaTinv.multiply(temp);
	BigDecimal GUARD = new BigDecimal(2.0);

	BigDecimal i;
	BigDecimal t;
	BigDecimal ax, ay, az, gx, gy, gz, avrms;
	BigDecimal rx, ry, rz, rzp, gxp, gyp, gzp;
	BigDecimal px, py, pz, ctx, cty, ctz, peakt_gz, stz;
	*/
	
	final int Positive = 0;
	final int Negative = 1;
	final double DeltaT = 0.01;
	final double DeltaTinv = 100.0;
	final int COUNTTHR = 5;
	final int COUNTLIM = 100;
	final double ROTTHR = 35.0 * DeltaTinv;
	final double GUARD = 5.0;
	
	int i;
	double t;	
	double ax, ay, az, gx, gy, gz, avrms;
	double rx, ry, rz, rzp, gxp, gyp, gzp;
	int px, py, pz, ctx, cty, ctz;
	int peakt_gz, stz;
	
	
	String result;
	int count = 0;
	double firstNumX;
	double firstNumZ;
	double secondNumX;
	double secondNumZ;
	double thirdNumX;
	double thirdNumZ;
	String first;
	String firstX;
	String second;
	String secondX;
	String third;
	String thirdX;
	
	public SiftParameter() {
		t = 0.0;
		i = 0;
		ax = ay = az = 0.0;
		gx = gy = gz = 0.0;
		avrms = 0.0;
		ctx = cty = ctz = 0;
		rx = ry = rz = 0.0;
		px = py = pz = Positive;
		gxp = gyp = gzp = 0.0;
		rzp = 0.0;
		peakt_gz = stz = 0;
		
	}
	
	public void calcSift() {
		if (pz == Positive) {
			if (gz >= GUARD) {
				if (gz > gzp) {
					peakt_gz = i;
					gzp = gz;
				}

				rz += gz;
				ctz++;
			} else {
				if ((ctz >= COUNTTHR) & (rz >= ROTTHR) && (ctz <= COUNTLIM)) {
					System.out.println(t + " RZ+: " + rz * DeltaT + " Duration "
							+ ctz + " Peak " + gzp + " PeakTiming "
							+ (double) peakt_gz * DeltaT + " RiseTime "
							+ (double) (peakt_gz - stz) * DeltaT);
					
				}
				gzp = 0.0;
				ctz = 0;
				rz = 0.0;
				rzp = 0.0;
				rx = 0.0; // synchronized with gz XŽ²Žü‚è‚Ì‰ñ“]Šp‚ÍAgz‚Ìƒ[ƒ“_Œð·Žž‚æ‚èÏŽZ‚·‚é
				pz = Negative;
				stz = i;
			}
		} else {
			if (gz >= (-GUARD)) {
				if ((ctz < (-COUNTTHR)) && (rz < (-ROTTHR))
						&& (ctz >= (-COUNTLIM))) {
					System.out.println(t + " RZ-: " + rz * DeltaT + " Duration "
							+ ctz + " Peak " + gzp + " PeakTiming "
							+ (double) peakt_gz * DeltaT + " RiseTime "
							+ (double) (peakt_gz - stz) * DeltaT + " Xangle "
							+ rx * DeltaT + " PeakAngle " + rzp * DeltaT);
					
					if (count == 0) {
						BigDecimal tmpZ = new BigDecimal(rz * DeltaT);
						tmpZ = tmpZ.setScale(1, BigDecimal.ROUND_HALF_UP);
						BigDecimal tmpX = new BigDecimal(rx * DeltaT);
						tmpX = tmpX.setScale(1, BigDecimal.ROUND_HALF_UP);
						
						first = "rz: " + Double.toString(Math.abs(tmpZ.doubleValue())) + "(deg)";
						firstX = "rx: " + Double.toString(Math.abs(tmpX.doubleValue())) + "(deg)";
						firstNumX = Math.abs(tmpX.doubleValue());
						firstNumZ = Math.abs(tmpZ.doubleValue());
					}
					if (count == 1) {
						BigDecimal tmpZ = new BigDecimal(rz * DeltaT);
						tmpZ = tmpZ.setScale(1, BigDecimal.ROUND_HALF_UP);
						BigDecimal tmpX = new BigDecimal(rx * DeltaT);
						tmpX = tmpX.setScale(1, BigDecimal.ROUND_HALF_UP);
						
						second = "rz: " + Double.toString(Math.abs(tmpZ.doubleValue())) + "(deg)";
						secondX = "rx: " + Double.toString(Math.abs(tmpX.doubleValue())) + "(deg)";
						secondNumX = Math.abs(tmpX.doubleValue());
						secondNumZ = Math.abs(tmpZ.doubleValue());
					}
					if (count == 2) {
						BigDecimal tmpZ = new BigDecimal(rz * DeltaT);
						tmpZ = tmpZ.setScale(1, BigDecimal.ROUND_HALF_UP);
						BigDecimal tmpX = new BigDecimal(rx * DeltaT);
						tmpX = tmpX.setScale(1, BigDecimal.ROUND_HALF_UP);
						
						third = "rz: " + Double.toString(Math.abs(tmpZ.doubleValue())) + "(deg)";
						thirdX = "rx: " + Double.toString(Math.abs(tmpX.doubleValue())) + "(deg)";
						thirdNumX = Math.abs(tmpX.doubleValue());
						thirdNumZ = Math.abs(tmpZ.doubleValue());
					}
					count++;
					if (count == 3) {
						double aveX = (firstNumX + secondNumX + thirdNumX) / 3;
						double aveZ = (firstNumZ + secondNumZ + thirdNumZ) / 3;
						
						firstNumX = firstNumX - aveX;
						firstNumZ = firstNumZ - aveZ;
						secondNumX = secondNumX - aveX;
						secondNumZ = secondNumZ - aveZ;
						thirdNumX = thirdNumX - aveX;
						thirdNumZ = thirdNumZ - aveZ;
						
						firstNumX = firstNumX * firstNumX;
						firstNumZ = firstNumZ * firstNumZ;
						secondNumX = secondNumX * secondNumX;
						secondNumZ = secondNumZ * secondNumZ;
						thirdNumX = thirdNumX * thirdNumX;
						thirdNumZ = thirdNumZ * thirdNumZ;
						
						double stdX = Math.sqrt(firstNumX + secondNumX + thirdNumX);
						double stdZ = Math.sqrt(firstNumZ + secondNumZ + thirdNumZ);
						
						double resultX = 1 - (stdX / aveX);
						double resultZ = 1 - (stdZ / aveZ);
						
						BigDecimal temp = new BigDecimal(resultX * resultZ * 100);
						temp = temp.setScale(1, BigDecimal.ROUND_HALF_UP);
						
						result = temp.toString() + " points!";
					}
				}
				ctz = 0;
				rz = 0.0;
				gzp = 0.0;
				pz = Positive;
				stz = i;
			} else {
				if (gz < gzp) {
					peakt_gz = i;
					gzp = gz;
					rzp = (rz + gz);
				}
				ctz--;
				rz += gz;
				rx += gx;
			}
		}
		i++;
	}
	 
}
