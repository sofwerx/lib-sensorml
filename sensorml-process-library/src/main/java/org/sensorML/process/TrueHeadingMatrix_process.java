/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML AbstractProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the
 
 Contributor(s): 
    Alexandre Robin <robin@nsstc.uah.edu>
    Gregoire Berthiau <berthiau@nsstc.uah.edu>
 
******************************* END LICENSE BLOCK ***************************/

package org.sensorML.process;
import net.opengis.swe.v20.DataArray;
import net.opengis.swe.v20.DataBlock;
import net.opengis.swe.v20.DataRecord;
import org.vast.data.*;
import org.vast.math.*;
import org.vast.process.*;
import org.vast.sensorML.ExecutableProcessImpl;


/**
 * <p>
 * Construct a 3D matrix of transformation from an orientation where the  
 * upAxis is -nadir, forwardAxis orthogonal to upAxis and in the plane of the
 * true heading and the third axis chosen so that the result XYZ reference
 * frame is direct.
 * </p>
 *
 * @author Alexandre Robin
 * @author Gregoire Berthiau
 * @date Apr 20, 2007
 */

public class TrueHeadingMatrix_process extends ExecutableProcessImpl
{
    private DataValue longitude, latitude, trueHeading;
    private DataArray outputMatrix; 
    
    public TrueHeadingMatrix_process()
    {    	
    }

    
    @Override
    public void init() throws SMLProcessException
    {
        try
        {
            // input mappings
            DataRecord locVector = (DataRecord)inputData.getComponent("lonLatTrueHeading");
            longitude = (DataValue)locVector.getComponent("longitude");
            latitude = (DataValue)locVector.getComponent("latitude");
            trueHeading = (DataValue)locVector.getComponent("trueHeading");            
            
            // output mapping
            outputMatrix = (DataArray)outputData.getComponent("posMatrix");
    }
    
        catch (Exception e)
        {
            throw new SMLProcessException(ioError, e);
        }
    }
    

    @Override
    public void execute() throws SMLProcessException
    {
        double lon = 0.0;
        double lat = 0.0;
        double trueHD = 0.0;
        
        if (longitude != null)
        	lon = longitude.getData().getDoubleValue();
        	
        if (latitude != null)
        	lat = latitude.getData().getDoubleValue();

        if (trueHeading != null)
        	trueHD = trueHeading.getData().getDoubleValue();
        
        Matrix3d matrix = new Matrix3d();
        matrix.setIdentity();// return identity matrix
        
        // System.out.println("lon " + lon + " lat " + lat + " trueheading " + trueHD);

        //Greg's without inverse
        matrix.rotateZ(-(Math.PI - trueHD));
        matrix.rotateY((Math.PI/2.0 - lat));
        matrix.rotateZ(-lon);
        
        // assign values to output matrix
        DataBlock data = outputMatrix.getData();
        for (int i=0; i<9; i++)
            data.setDoubleValue(i, matrix.getElement(i/3, i%3));
  
    }
}