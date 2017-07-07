package fr.insalyon.creatis.vip.cli.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "InfoExecution")
public class InfoExecution implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Id;
	@Column(name = "ExecutionIdentifier")
	private String executionIdentifier;
	@Column(name="PipelineIdentifier")
	private String pipelineIdentifier;
	@Column(name = "Status")
	private String status;
	@Column(name = "Repersitory")
	private String repersitory;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "StartDate")
	private Date startdate;
	
	
	public InfoExecution() {

	}

	public InfoExecution(String executionIdentifier,String pipelineIdentifier, String status, String repersitory, Date startdate) {
		super();
		this.executionIdentifier = executionIdentifier;
		this.pipelineIdentifier=pipelineIdentifier;
		this.status = status;
		this.repersitory = repersitory;
		this.startdate = startdate;
	}

	

	@Override
	public String toString() {
		return "***InfoExecution***\r\n executionIdentifier=" + executionIdentifier + "\r\n pipelineIdentifier="
				+ pipelineIdentifier + "\r\n status=" + status + "\r\n repersitory=" + repersitory + "\r\n startdate="
				+ startdate + "";
	}

	public String getPipelineIdentifier() {
		return pipelineIdentifier;
	}

	public void setPipelineIdentifier(String pipelineIdentifier) {
		this.pipelineIdentifier = pipelineIdentifier;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((executionIdentifier == null) ? 0 : executionIdentifier.hashCode());
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InfoExecution other = (InfoExecution) obj;
		if (executionIdentifier == null) {
			if (other.executionIdentifier != null)
				return false;
		} else if (!executionIdentifier.equals(other.executionIdentifier))
			return false;
		return true;
	}

	public String getExecutionIdentifier() {
		return executionIdentifier;
	}

	public void setExecutionIdentifier(String executionIdentifier) {
		this.executionIdentifier = executionIdentifier;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRepersitory() {
		return repersitory;
	}

	public void setRepersitory(String repersitory) {
		this.repersitory = repersitory;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

}
