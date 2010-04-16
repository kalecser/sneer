package sneer.bricks.snapps.gis.map.impl;

import static sneer.foundation.environments.Environments.my;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import sneer.bricks.network.computers.httpgateway.HttpGateway;
import sneer.bricks.snapps.gis.location.Location;
import sneer.bricks.snapps.gis.map.MapRenderer;
import sneer.foundation.lang.Consumer;

class MapRendererImpl implements MapRenderer{

	private int _zoom = 10;
	private int _mapSize = 600;

	@Override
	public void render(Consumer<Image> receiver, Location location) {
		render(receiver, location, _zoom);
	}
	
	@Override
	public void render(final Consumer<Image> receiver, final Location location, final int zoom) {
		String httpUrl = "http://maps.google.com/staticmap?center=" + location.latitude() + "," + location.longitude() + "&zoom=" + zoom + "&size=" + _mapSize + "x" + _mapSize
						  + "&key=ABQIAAAAipu2vgwNjShyGzhINGjXvRT2yXp_ZAY8_ufC3CFXhHIE1NvwkxTKgtleBywtdYBkXFEBvmkPMqvBzg";
		
		my(HttpGateway.class).get(httpUrl, new Consumer<byte[]>(){ @Override public void consume(byte[] bytes) {
			receiver.consume(bytesToImage(bytes));
		}});
	}

	private Image bytesToImage(byte[] imageData) {
		BufferedImage image = decode(imageData);
		Graphics2D g2d=((Graphics2D)image.getGraphics());
		g2d.setColor(Color.green);
		
		g2d.fillOval(_mapSize/2-5, _mapSize/2-5,10,10);
		g2d.setColor(Color.black);
		g2d.drawOval(_mapSize/2-5, _mapSize/2-5,10,10);
		return image;
	}

	private BufferedImage decode(byte[] imageData) {
		try {
			return ImageIO.read(new ByteArrayInputStream(imageData));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}