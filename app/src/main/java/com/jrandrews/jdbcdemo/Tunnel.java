package com.jrandrews.jdbcdemo;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ClientChannel;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.common.channel.Channel;
import org.apache.sshd.common.config.keys.loader.KeyPairResourceLoader;
import org.apache.sshd.common.keyprovider.KeyIdentityProvider;
import org.apache.sshd.common.session.SessionHeartbeatController;
import org.apache.sshd.common.util.net.SshdSocketAddress;
import org.apache.sshd.common.util.security.SecurityUtils;
import org.apache.sshd.server.forward.AcceptAllForwardingFilter;

import com.jrandrews.jdbcdemo.Configuration.Item;
/**
 * Create an SSH tunnel.
 * 
 * <a href="https://github.com/apache/mina-sshd/blob/master/docs/port-forwarding.md">Apache Mina-sshd</a>
 */
public class Tunnel implements Closeable {
    private SshClient client = null;
    private ClientSession session = null;
    private ClientChannel channel = null;

    public void start() throws IOException, GeneralSecurityException {
        if (client != null || channel != null || session != null) {
            return;
        }
        
        final Configuration config = new Configuration();

        client = SshClient.setUpDefaultClient();
        // client.setServerKeyVerifier(new KnownHostsServerKeyVerifier(null, null));

        KeyPairResourceLoader loader = SecurityUtils.getKeyPairResourceParser();
        Collection<KeyPair> keys = loader.loadKeyPairs(null, Path.of(config.get(Item.BASTION_PRIVATE_KEY_FILENAME)), null);

        client.setKeyIdentityProvider(KeyIdentityProvider.wrapKeyPairs(keys));
        client.setForwardingFilter(AcceptAllForwardingFilter.INSTANCE);
        client.setSessionHeartbeat(SessionHeartbeatController.HeartbeatType.IGNORE, TimeUnit.MILLISECONDS, 15000);

        client.start();

        session = client.connect(config.get(Item.BASTION_USERNAME), config.get(Item.BASTION_HOSTNAME), 22)
                .verify(15l, TimeUnit.SECONDS)
                .getSession();
        session.auth().verify(15l, TimeUnit.SECONDS);

        channel = session.createChannel(Channel.CHANNEL_SHELL);
        channel.open().verify(10, TimeUnit.SECONDS);

        session.startLocalPortForwarding(config.getInt(Item.TUNNEL_LOCAL_PORT),
                new SshdSocketAddress(config.get(Item.TUNNEL_TARGET_HOSTNAME), config.getInt(Item.TUNNEL_TARGET_PORT)));
    }

    @Override
    public void close() throws IOException {
        if (channel != null) {
            try {
                channel.close(false);
            } catch (Exception e) {
                // no action
            }
        }
        if (session != null) {
            try {
                session.close();
            } catch (Exception e) {
                // no action
            }
        }
        if (client != null) {
            try {
                client.stop();
            } catch (Exception e) {
                // no action
            }
        }
    }
}
