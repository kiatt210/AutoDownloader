package hu.kiss.seeder.client.qbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileInfo {

	@SerializedName("availability")
	@Expose
	private long availability;
	@SerializedName("index")
	@Expose
	private long index;
	@SerializedName("name")
	@Expose
	private String name;

	public long getAvailability() {
		return availability;
	}

	public void setAvailability(long availability) {
		this.availability = availability;
	}

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
