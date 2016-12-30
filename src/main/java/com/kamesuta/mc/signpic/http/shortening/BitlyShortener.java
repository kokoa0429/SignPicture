package com.kamesuta.mc.signpic.http.shortening;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.annotation.Nonnull;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import com.google.gson.stream.JsonReader;
import com.kamesuta.mc.signpic.Client;
import com.kamesuta.mc.signpic.http.Communicate;
import com.kamesuta.mc.signpic.http.CommunicateResponse;
import com.kamesuta.mc.signpic.state.Progressable;
import com.kamesuta.mc.signpic.state.State;
import com.kamesuta.mc.signpic.util.Downloader;

public @Nonnull class BitlyShortener extends Communicate implements Progressable, IShortener {
	protected ShorteningRequest shortreq;
	protected String key;
	protected BitlyResult result;

	public @Nonnull  BitlyShortener(final @Nonnull ShorteningRequest shortreq, final @Nonnull String key) {
		this.shortreq = shortreq;
		this.key = key;
	}

	@Override
	public @Nonnull State getState() {
		return this.shortreq.getState("§dbit.ly: §r%s");
	}

	@Override
	public void communicate() {
		final String url = "https://api-ssl.bitly.com/v3/shorten?access_token=%s&longUrl=%s";

		InputStream resstream = null;
		JsonReader jsonReader1 = null;
		try {
			setCurrent();
			// create the get request.
			final HttpGet httpget = new HttpGet(String.format(url, this.key, this.shortreq.getLongURL()));

			// execute request
			final HttpResponse response = Downloader.downloader.client.execute(httpget);

			if (response.getStatusLine().getStatusCode()==HttpStatus.SC_OK) {
				final HttpEntity resEntity = response.getEntity();
				if (resEntity!=null) {
					resstream = resEntity.getContent();
					this.result = Client.gson.<BitlyResult> fromJson(jsonReader1 = new JsonReader(new InputStreamReader(resstream, Charsets.UTF_8)), BitlyResult.class);
					onDone(new CommunicateResponse(this.result!=null&&this.result.status_code==HttpStatus.SC_OK, null));
					return;
				}
			} else {
				onDone(new CommunicateResponse(false, new IOException("Bad Response")));
				return;
			}
		} catch (final Exception e) {
			onDone(new CommunicateResponse(false, e));
			return;
		} finally {
			unsetCurrent();
			IOUtils.closeQuietly(resstream);
			IOUtils.closeQuietly(jsonReader1);
		}
		onDone(new CommunicateResponse(false, null));
		return;
	}

	public @Nonnull static class BitlyResult {
		public @Nonnull Data data;
		public @Nonnull int  status_code;
		public @Nonnull String status_txt;

		public @Nonnull  static class Data {
			public @Nonnull String long_url;
			public @Nonnull String url;
			public @Nonnull String hash;
			public @Nonnull String global_hash;
			public @Nonnull String new_hash;
		}
	}

	@Override
	public @Nonnull String getShortLink() {
		if (this.result!=null&&this.result.data!=null)
			return this.result.data.url;
		return null;
	}
}
