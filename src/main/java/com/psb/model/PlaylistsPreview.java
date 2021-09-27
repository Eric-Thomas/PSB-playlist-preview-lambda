package com.psb.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PlaylistsPreview implements Serializable {
	
	private static final long serialVersionUID = 8201458729923746288L;
	
	@JsonProperty("items")
	List<SpotifyPlaylist> playlists;
	String next;

}
