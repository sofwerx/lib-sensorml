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
 * Generates an homogeneous matrix corresponding to an orientation
 * where the upAxis is aligned with the geocentric direction,
 * forwardAxis orthogonal to upAxis and in the plane of the velocity
 * vector and the third axis chosen so that the result XYZ reference
 * frame is direct.
 * </p>
 *
 * @author Alexandre Robin
 * @date Sep 2, 2005
 */
public class GeocentricPointingMatrix_Process extends ExecutableProcessImpl
{
    private DataValue xData, yData, zData;
    private DataValue vxData, vyData, vzData;
    private DataArray outputMatrix;
    char forwardAxis, upAxis;
    
    
    public GeocentricPointingMatrix_Process()
    {    	
    }

    
    @Override
    public void init() throws SMLProcessException
    {
        try
        {
            // input mappings
            DataRecord locVector = (DataRecord)inputData.getComponent("location");
            xData = (DataValue)locVector.getComponent("x");
            yData = (DataValue)locVector.getComponent("y");
            zData = (DataValue)locVector.getComponent("z");            
            DataRecord velVector = (DataRecord)inputData.getComponent("velocity");
            vxData = (DataValue)velVector.getComponent("x");
            vyData = (DataValue)velVector.getComponent("y");
            vzData = (DataValue)velVector.getComponent("z");
            
            // output mapping
            outputMatrix = (DataArray)outputData.getComponent("posMatrix");
            
            // read parameters
            upAxis = paramData.getComponent("upAxis").getData().getStringValue().charAt(0);
            forwardAxis = paramData.getComponent("forwardAxis").getData().getStringValue().charAt(0);
        }
        catch (Exception e)
        {
            throw new SMLProcessException(ioError, e);
        }
    }
    

    @Override
    public void execute() throws SMLProcessException
    {
        double x = 0.0;
        double y = 0.0;
        double z = 0.0;
        double vx = 0.0;
        double vy = 0.0;
        double vz = 0.0;
        
        if (xData != null)
            x = xData.getData().getDoubleValue();

        if (yData != null)
            y = yData.getData().getDoubleValue();

        if (zData != null)
            z = zData.getData().getDoubleValue();

        if (vxData != null)
            vx = vxData.getData().getDoubleValue();

        if (vyData != null)
            vy = vyData.getData().getDoubleValue();

        if (vzData != null)
            vz = vzData.getData().getDoubleValue();

        Matrix4d newMatrix = null;        
        Vector3d heading = new Vector3d();
        Vector3d other = new Vector3d();
        
        Vector3d up = new Vector3d(x, y, z);
        up.normalize();
        
        Vector3d velocity = new Vector3d(vx, vy, vz);
        velocity.normalize();

        if ((forwardAxis == 'X') && (upAxis == 'Z'))
        {
            other.cross(up, velocity);
            other.normalize();
            heading.cross(other, up);
            newMatrix = new Matrix4d(heading, other, up);
        }

        else if ((forwardAxis == 'X') && (upAxis == 'Y'))
        {
            other.cross(velocity, up);
            other.normalize();
            heading.cross(up, other);
            newMatrix = new Matrix4d(heading, up, other);
        }

        else if ((forwardAxis == 'Y') && (upAxis == 'X'))
        {
            other.cross(up, velocity);
            other.normalize();
            heading.cross(other, up);
            newMatrix = new Matrix4d(up, heading, other);
        }

        else if ((forwardAxis == 'Y') && (upAxis == 'Z'))
        {
            other.cross(velocity, up);
            other.normalize();
            heading.cross(up, other);
            newMatrix = new Matrix4d(other, heading, up);
        }

        else if ((forwardAxis == 'Z') && (upAxis == 'X'))
        {
            other.cross(velocity, up);
            other.normalize();
            heading.cross(up, other);
            newMatrix = new Matrix4d(up, other, heading);
        }

        else if ((forwardAxis == 'Z') && (upAxis == 'Y'))
        {
            other.cross(up, velocity);
            other.normalize();
            heading.cross(other, up);
            newMatrix = new Matrix4d(other, up, heading);
        }
        
        newMatrix.setTranslation(x, y, z);
        
        // assign values to output matrix
        DataBlock data = outputMatrix.getData();
        for (int i=0; i<16; i++)
            data.setDoubleValue(i, newMatrix.getElement(i/4, i%4));
    }
}