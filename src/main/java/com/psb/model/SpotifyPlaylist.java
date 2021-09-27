package com.psb.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class SpotifyPlaylist implements Serializable {

	private static final long serialVersionUID = -7982164757913098056L;
	
	private String name;
	private List<SpotifyImage> images;
	private String id;

}
