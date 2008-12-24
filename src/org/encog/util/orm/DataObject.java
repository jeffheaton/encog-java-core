package org.encog.util.orm;

import java.io.Serializable;
import java.util.Date;
 
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
 
@MappedSuperclass
public abstract class DataObject implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7032283205435194035L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id = null;
 
    @Version
    @Column(name = "version")
    private int version = 0;
 
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_update")
    private Date lastUpdate = new Date();
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();
 
    protected void copy(final DataObject source)
    {
        this.id = source.id;
        this.version = source.version;
        this.lastUpdate = source.lastUpdate;
    }
 
    @Override
    public boolean equals(final Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof DataObject))
        {
            return false;
        }
        final DataObject other = (DataObject) obj;
        if (this.id != null && other.id != null)
        {
            if (this.id != other.id)
            {
                return false;
            }
        }
        return true;
    }
 
    protected static boolean getBooleanValue(final Boolean value)
    {
        return Boolean.valueOf(String.valueOf(value));
    }
    
    public abstract void validate();
 
    public Long getId()
    {
        return this.id;
    }
 
    @SuppressWarnings("unused")
    private void setId(final Long id)
    {
        this.id = id;
    }
 
    public int getVersion()
    {
        return this.version;
    }
 
    @SuppressWarnings("unused")
    private void setVersion(final int version)
    {
        this.version = version;
    }
 
    public Date getLastUpdate()
    {
        return this.lastUpdate;
    }
 
    public void setLastUpdate(final Date lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
    
    
  
}