package com.plutomayhem.mobile;

import android.hardware.usb.*;
import java.io.Closeable;
import java.io.IOException;

/**
 * USB bulk transport for Pluto's composite USB device.
 * This layer does not implement the IIO protocol and never transmits RF itself.
 * The caller must select the correct IIO endpoint pair from validated descriptors.
 */
public final class PlutoUsbBulkTransport implements Closeable {
    private final UsbDeviceConnection connection;
    private final UsbInterface usbInterface;
    private final UsbEndpoint input;
    private final UsbEndpoint output;
    private boolean closed;

    public PlutoUsbBulkTransport(UsbDeviceConnection connection, UsbInterface usbInterface,
                                 int inputAddress, int outputAddress) throws IOException {
        if (connection == null || usbInterface == null) throw new IOException("No USB connection");
        this.connection = connection;
        this.usbInterface = usbInterface;
        UsbEndpoint in = null, out = null;
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint endpoint = usbInterface.getEndpoint(i);
            if (endpoint.getType() != UsbConstants.USB_ENDPOINT_XFER_BULK) continue;
            if (endpoint.getAddress() == inputAddress &&
                endpoint.getDirection() == UsbConstants.USB_DIR_IN) in = endpoint;
            if (endpoint.getAddress() == outputAddress &&
                endpoint.getDirection() == UsbConstants.USB_DIR_OUT) out = endpoint;
        }
        if (in == null || out == null) throw new IOException("Invalid bulk endpoint pair");
        if (!connection.claimInterface(usbInterface, false)) throw new IOException("USB interface busy");
        input = in;
        output = out;
    }

    public synchronized int read(byte[] data, int timeoutMs) throws IOException {
        ensureOpen();
        if (data == null || data.length == 0 || timeoutMs <= 0) throw new IllegalArgumentException();
        int n = connection.bulkTransfer(input, data, data.length, timeoutMs);
        if (n < 0) throw new IOException("USB bulk IN failed or timed out");
        return n;
    }

    public synchronized int write(byte[] data, int timeoutMs) throws IOException {
        ensureOpen();
        if (data == null || data.length == 0 || timeoutMs <= 0) throw new IllegalArgumentException();
        int offset = 0;
        while (offset < data.length) {
            byte[] chunk = new byte[Math.min(16384, data.length - offset)];
            System.arraycopy(data, offset, chunk, 0, chunk.length);
            int n = connection.bulkTransfer(output, chunk, chunk.length, timeoutMs);
            if (n <= 0) throw new IOException("USB bulk OUT failed or timed out");
            offset += n;
        }
        return offset;
    }

    private void ensureOpen() throws IOException {
        if (closed) throw new IOException("USB transport closed");
    }

    @Override public synchronized void close() {
        if (!closed) {
            closed = true;
            connection.releaseInterface(usbInterface);
        }
    }
}
