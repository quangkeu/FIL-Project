
/*
 * @input:
 * 	A1: rate of packets having IAT <= 0.2ms
 * 	A2: rate of flow having only 1 packet per flow
 * @output:
 * 	Z: rate of incoming flows will be dropped
 */

public class FuzzyLogic {
	public static double FIS(double A1, double A2) {
		double z = 0;
		
		double a = 0.0;
		double b = 0.0;
		double c = 0.07;
		double d = 0.9;
		double a1 = 0.07;
		double b1 = 0.9;
		double c1 = 1.0;
		double d1 = 1.0;
		double e = 0.0;
		double f = 0.0;
		double g = 0.05;
		double h = 0.8;
		double e1 = 0.05;
		double f1 = 0.8;
		double g1 = 1.0;
		double h1 = 1.0;
		double A, B, C, D;
		
		if (b - a == 0) {
			A = 1000;
		}
		else {
			A = (A1 - a)/(b - a);
		}
		
		if (b1 - a1 == 0) {
			C = 1000;
		}
		else {
			C = (A1 - a1)/(b1 - a1);
		}
		
		if (d - c == 0) {
			B = 1000;
		}
		else {
			B = (d - A1)/(d - c);
		}
		
		if (d1 - c1 == 0) {
			D = 1000;
		}
		else {
			D = (d1 - A1)/(d1 - c1);
		}
		
		double q1 = min(A, 1.0);
		double Z3 = min(q1, B);
		double Fl1 = max(Z3, 0.0);
		double q2 = min(C, 1.0);
		double Z4 = min(q2, D);
		double Fh1 = max(Z4, 0.0);
		double E, F, G, H;
		
		if (f - e == 0) { 
			E = 1000;   
		} 
		else {  
			E = (A2 - e) / (f - e);
		}     
		
		if (h - g == 0) {
			F = 1000;
		} 
		else {
			F = (h - A2) / (h - g);
		}
		
		if (f1 - e1 == 0) {     
			G = 1000;    
		} 
		else {      
			G = (A2 - e1) / (f1 - e1);  
		}
		
		if (h1 - g1 == 0) {
			H = 1000;
		}
		else {
			H = (h1 - A2) / (h1 - g1);
		}

		double q3 = min(E, 1.0);
		double Z1 = min(q3, F);
		double Fl2 = max(Z1, 0.0);  
		double q4 = min(G, 1.0);
		double Z2 = min(q4, H);  
		double Fh2 = max(Z2, 0.0);
		double W1 = min(Fl1, Fl2);  
		double W2 = min(Fl1, Fh2); 
		double W3 = min(Fh1, Fl2); 
		double W4 = min(Fh1, Fh2);

		if (((A1 >= 0.99) && (A1 <= 1.0)) || ((A2 >= 0.9) && (A2 <= 1.0))) {
			z = 1; 
		} 
		else if (((A1 >= 0.0) && (A1 <= 0.8)) && ((A2 >= 0.0) && (A2 <= 0.15))) { 
			z = 0;
		} 
		else { 
			z = (W2 + W3 + W4) / (W1 + W2 + W3 + W4);
		}
			return z; 
		
	}
	
	private static double min(double x, double y) {
		double min;
		return (min = (x < y) ? x : y);
	}
	
	private static double max(double x, double y) {
		double max;
		return (max = (x > y) ? x : y);
	}
	
	
}
