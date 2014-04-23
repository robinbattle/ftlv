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
 * Abstract base class for factorizing a nonnegative matrix X as the product
 * of two nonnegative matrices:
 *
 * X \approx W * H
 *
 * Usage:
 * First initialized the matrices in the above factorization outside of this class.
 * Then, get a new instance of this class by calling the construct with these
 * matrices. You may then repeatedly invoke <i>doLeftUpdate()</i> and/or <i>doRightUpdate</i>
 * as many times as needed to reach convergence. You can check the convergence status
 * at any time by calling <i>getReconstructionError</i> to get the current RMSE.
 * Note that since references for the matrices are passed into the constructor, it is
 * not necessary to supply the matrices when calling the various public methods.
 *
 * @author Brian K. Vogel (brian@brianvogel.com)
 */
public abstract class NMF {


    /**
     * Create a new NMF factorizer for the factorization:
     * X = W * H
     * 
     * The supplied matrices must have already been initialized and must have
     * consistant dimensions. Otherwise a runtime exception will occur.
     * 
     *
     * @param X An M x N matrix in the factorization X = W*H.
     * @param W An M x R matrix in the factorization X = W*H.
     * @param H An R x N matrix in the factorization X = W*H.
     * @param instanceName
     * @throws RuntimeException If the matrices have inconsistant dimensions.
     */
    public NMF(Matrix X, Matrix W, Matrix H, String instanceName) {
        // Check consitency:
        if (X.height != W.height) {
            throw new RuntimeException("Error: X and W don't have the same number of rows!");
        } else if (X.width != H.width) {
            throw new RuntimeException("Error: X and H don't have the same number of columns!");
        } else if (W.width != H.height) {
            throw new RuntimeException("Error: Number of columns in W does not equal number of rows in H!");
        }
        _X = X;
        _W = W;
        _H = H;
        _TempSizeX = new Matrix(_X.height, _X.width);
        _name = instanceName;
    }

    /**
     * Perform an NMF update step to compute a new estimate for W given the
     * previous values for X, W, and H. This method thus modifies W but does not
     * modify X and H.
     *
     * This method is left abstract so that subclasses can implement NMF updates for
     * optimizing various cost functions.
     *
     * @param X The X matrix in X \approx W * H
     * @param W The W factor matrix. The updated value is returned in the reference
     * to W that was passed to the constructor.
     * @param H The H factor matrix.
     *
     */
    public abstract void doLeftUpdate();

    /**
     * Perform an NMF update step to compute a new estimate for H given the
     * previous values for X, W, and H. This method thus modifies H but does not
     * modify X and W.
     *
     * This method is left abstract so that subclasses can implement NMF updates for
     * optimizing various cost functions.
     *
     * @param X The X matrix in X \approx W * H
     * @param W The W factor matrix.
     * @param H The H factor matrix. The updated value is returned in the reference
     * to H that was passed to the constructor.
     *
     */
    public abstract void doRightUpdate();

    /**
     * Perform a data generation step to replace X by the current factorization approximation.
     * That is, update X as the value W * H. This method thus modifies X but does not
     * modify W and H.
     *
     */
    public void doDataGeneration() {
        // _X <- _W * _H
        MatrixUtilities.matMultiply(_X, _W, _H);
    }

    /**
     * Compute the root mean squared error (RMSE) for the factorization approximation and return
     * it.
     *
     * Note: The RMSE is most appropriate when using a Euclidean cost function. Therefore, a
     * subclass that optimizes a different cost function might want to override this method
     * to compute the reconstruction error differently.
     *
     * @return The current RMSE
     */
    public float getReconstructionError() {
        // _TempSizeX <- _W * _H
        MatrixUtilities.matMultiply(_TempSizeX, _W, _H);
        // _TempSizeX <- _TempSizeX - X
        MatrixUtilities.elementWiseDifference(_TempSizeX, _TempSizeX, _X);
        // _TempSizeX <- _TempSizeX .^2
        MatrixUtilities.elementWiseSquare(_TempSizeX);
        // 1 /sqrt(M*N)
        float rowsX = (float)_TempSizeX.height;
        float colsX = (float)_TempSizeX.width;
        float sum = MatrixUtilities.sum(_TempSizeX);
        return (float)java.lang.Math.sqrt(sum/(rowsX*colsX));
    }

    /**
     * Print out the current reconstruction error.
     *
     */
    public void printReconstructionError() {
        float error = getReconstructionError();
        System.out.println(_name + ": Error = " + error);
    }

    // protected members
    protected Matrix _X;
    protected Matrix _W;
    protected Matrix _H;
    // Temporary matrices (subclasses can use these, too)
    protected Matrix _TempSizeX; // Same size as _X
    protected String _name;

}
