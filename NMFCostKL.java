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
 * Factor a nonnegative matrix X as the product of two nonnegative matrices:
 *
 * X \approx W * H
 *
 * using Lee and Seung's multiplicative update rules for minimizing the
 * generalized KL divergence cost function. I have modified these rules slightly
 * for improved numerical stability by adding a small constant to the numerator and
 * denominator when performing the element-wise matrix division operations.
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
public class NMFCostKL extends NMF {

    public NMFCostKL(Matrix X, Matrix W, Matrix H, String instanceName) {
        super(X, W, H, instanceName);
        // Initialize temporary matrices
        _TempSizeX2 = new Matrix(_X.height, _X.width);
        _TempSizeWTran = new Matrix(_W.width, W.height);
        _TempSizeH = new Matrix(_H.height, _H.width);
        _TempSizeH2 = new Matrix(_H.height, _H.width);
        _OnesSizeX = new Matrix(_X.height, _X.width);
        // Fill with 1s.
        MatrixUtilities.setValue(_OnesSizeX, 1.0f);
        _TempSizeHTran = new Matrix(_H.width, _H.height);
        _TempSizeW = new Matrix(_W.height, _W.width);
        _TempSizeW2 = new Matrix(_W.height, _W.width);
    }

    @Override
    public void doLeftUpdate() {
        // Compute X / (W*H) and return the result in _TempSizeX
        _XOverWH();
        // _TempSizeWTran <- W^T
        MatrixUtilities.transpose(_TempSizeWTran, _W);
        // _TempSizeH <- W^T * (X / (W * H))
        MatrixUtilities.matMultiply(_TempSizeH, _TempSizeWTran, _TempSizeX);
        // _TempSizeH2 <- W^T * _OnesSizeX
        MatrixUtilities.matMultiply(_TempSizeH2, _TempSizeWTran, _OnesSizeX);
        // _TempSizeH <- _TempSizeH / _TempSizeH2
        MatrixUtilities.elementWiseDivide(_TempSizeH, _TempSizeH, _TempSizeH2, _epsilon);
        // _H <- _H * _TempSizeH
        MatrixUtilities.elementWiseMultiply(_H, _H, _TempSizeH);
    }

    @Override
    public void doRightUpdate() {
        // Compute X / (W*H) and return the result in _TempSizeX
        _XOverWH();
        // _TempSizeHTran <- H^T
        MatrixUtilities.transpose(_TempSizeHTran, _H);
        // _TempSizeW <- _TempSizeX * H^T
        MatrixUtilities.matMultiply(_TempSizeW, _TempSizeX, _TempSizeHTran);
        // _TempSizeW2 <- _OnesSizeX * H^T
        MatrixUtilities.matMultiply(_TempSizeW2, _OnesSizeX, _TempSizeHTran);
        // _TempSizeW <- _TempSizeW / _TempSizeW2
        MatrixUtilities.elementWiseDivide(_TempSizeW, _TempSizeW, _TempSizeW2, _epsilon);
        // _W <- _W * _TempSizeW
        MatrixUtilities.elementWiseMultiply(_W, _W, _TempSizeW);
    }

    /**
     * Compute X / (W*H) and return the result in _TempSizeX
     */
    private void _XOverWH() {
        // _TempSizeX2 <- _W * _H
        MatrixUtilities.matMultiply(_TempSizeX2, _W, _H);
        // _TempSizeX <- X / _TempSizeX2
        MatrixUtilities.elementWiseDivide(_TempSizeX, _X, _TempSizeX2, _epsilon);
    }

    // Temporary matrices (subclasses can use these, too)
    protected Matrix _TempSizeX2; // Same size as _X
    protected Matrix _TempSizeWTran; // Same size as W transpose
    protected Matrix _TempSizeH; // Same size as H
    protected Matrix _TempSizeH2; // Same size as H
    protected Matrix _TempSizeHTran; // Same size as H transpose
    protected Matrix _OnesSizeX; // Same size as X and contains all 1s
    protected Matrix _TempSizeW; // Same size as W
    protected Matrix _TempSizeW2; // Same size as W
    private float _epsilon = 0.00001f;


}
