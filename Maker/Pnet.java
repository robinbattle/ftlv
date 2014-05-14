/*
 * Copyright (C) 2005-2011 Brian K. Vogel
 * 
 * This file is part of PNET.
 *
 *  PNET is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PNET is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PNET.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 *
 * @author Brian Vogel
 */
public class Pnet {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Test basic NMF implementation.
        testNMF1();
    }
    
    
    /*
     * Initialize matrices to random positive values and then perform a number of iterations
     * of Lee and Seung's KL divergence multiplicative update algorithm, printing the
     * recontruction error at each iteration.
     * 
     * Note that since that matrix X contains random values, we can't expect the reconstruction 
     * error to decrease much. The purpose of this method is simply to illustrate the usage
     * of the NMF class (and its subclasses).
     */
    public static void testNMF1() {

        int M = 100; // rows in X
        int T = 300; // columns in X
        int R = 5; // number of basis columns in W

        Matrix X = new Matrix(M, T);
        MatrixUtilities.randomize(X);
        Matrix W = new Matrix(M, R);
        MatrixUtilities.randomize(W);
        Matrix H = new Matrix(R, T);
        MatrixUtilities.randomize(H);

        NMFCostKL nmfSolver = new NMFCostKL(X, W, H, "solver1");

        int iterations = 200;
        for (int i = 0; i < iterations; i++) {

            nmfSolver.doLeftUpdate();
            nmfSolver.doRightUpdate();
            nmfSolver.printReconstructionError();

        }

    }
}
