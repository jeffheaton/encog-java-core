package org.encog.util.cl.kernels;

import static org.jocl.CL.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.encog.EncogError;
import org.encog.persist.location.ResourcePersistence;
import org.jocl.*;


/**
 * Defines a basic OpenCL kernal, as used by Encog.  Contains the 
 * kernal source code and a compiled program/kernal.
 */
public class EncogKernel {

    /// <summary>
    /// The source code for the kernel.
    /// </summary>
    private String cl;

    /// <summary>
    /// The OpenCL context.
    /// </summary>
    private cl_context context;

    /// <summary>
    /// The OpenCL program.
    /// </summary>
    private cl_program program;

    /// <summary>
    /// The OpenCL kernel.
    /// </summary>
    private cl_kernel kernel;

    /// <summary>
    /// The name of the function that should be called to execute 
    /// this kernel, from inside the OpenCL source code.
    /// </summary>
    private String kernelName;

    /// <summary>
    /// Create an Encog OpenCL kernel.  The Kernel will be loaded from an 
    /// embedded resource.
    /// </summary>
    /// <param name="context">The OpenCL context that this kernel 
    /// belongs to.</param>
    /// <param name="sourceName">The name of the kernel, from an embedded 
    /// resource.</param>
    /// <param name="kernelName">The name of the function, in the kernal, 
    /// called to start the kernel.</param>
    public EncogKernel(cl_context context, String sourceName, String kernelName)
    {
        ResourcePersistence resource = new ResourcePersistence(sourceName);
        this.context = context;
        this.kernelName = kernelName;
        this.cl = resource.loadString();
    }

    /// <summary>
    /// Compile the kernel with no preprocessor defines.
    /// </summary>
    public void compile()
    {
        compile(new HashMap<String,String>());
    }

    /// <summary>
    /// Compile the kernel with a map of preprocessor defines, a collection 
    /// of name-value pairs.
    /// </summary>
    /// <param name="options">A map of preprocessor defines.</param>
    public void compile(Map<String,String> options)
    {
        // clear out any old program
        //if (this.program != null)
        //    this.program.Dispose();

    	// Create the program from the source code
        cl_program program = clCreateProgramWithSource(context,
            1, new String[]{ this.cl }, null, null);
        
        if (options.size() > 0)
        {
            StringBuilder builder = new StringBuilder();
            for (Entry<String, String> obj : options.entrySet())
            {
                if (builder.length() > 0)
                    builder.append(" ");
                builder.append("-D ");
                builder.append(obj.getKey());
                builder.append("=");
                builder.append(obj.getValue());
            }

            clBuildProgram(program, 0, null, null, null, null);
        }
        else
            clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, this.kernelName, null);
    }

    /// <summary>
    /// Called internally to prepare to execute a kernel.
    /// </summary>
    public void prepareKernel()
    {
        if (this.kernel == null)
            throw new EncogError("Must compile CL kernel before using it.");
    }

    /// <summary>
    /// The OpenCL context that this kernel belongs to.
    /// </summary>
    public cl_context getContext()
    {
            return this.context;
    }

    /// <summary>
    /// The OpenCL program that the kernel belongs to.
    /// </summary>
    public cl_program getProgram()
    {
            return this.program;
    }

    /// <summary>
    /// The OpenCL kernel.
    /// </summary>
    public cl_kernel getKernel()
    {
            return this.kernel;
    }

	
}
