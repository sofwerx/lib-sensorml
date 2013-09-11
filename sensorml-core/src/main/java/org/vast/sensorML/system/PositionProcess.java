/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "SensorML DataProcessing Engine".
 
 The Initial Developer of the Original Code is the VAST team at the
 
 Contributor(s): 
 Alexandre Robin <robin@nsstc.uah.edu>
 
 ******************************* END LICENSE BLOCK ***************************/

package org.vast.sensorML.system;

import org.vast.process.DataProcess;


/**
 * <p><b>Title:</b><br/>
 * Position Process
 * </p>
 *
 * <p><b>Description:</b><br/>
 * SensorML Position Process backed up by a DataProcess
 * </p>
 *
 * <p>Copyright (c) 2005</p>
 * @author Alexandre Robin
 * @date Feb 17, 2006
 * @version 1.0
 */
public class PositionProcess extends Position
{
    protected DataProcess process;


    public DataProcess getProcess()
    {
        return process;
    }


    public void setProcess(DataProcess process)
    {
        this.process = process;
    }

}