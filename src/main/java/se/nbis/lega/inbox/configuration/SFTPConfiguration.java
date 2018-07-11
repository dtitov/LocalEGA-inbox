package se.nbis.lega.inbox.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.pubkey.PublickeyAuthenticator;
import org.apache.sshd.server.auth.pubkey.UserAuthPublicKeyFactory;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.subsystem.sftp.SftpEventListener;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Configuration
public class SFTPConfiguration {

    private int inboxPort;

    private SftpEventListener sftpEventListener;
    private PasswordAuthenticator passwordAuthenticator;
    private PublickeyAuthenticator publicKeyAuthenticator;

    @Bean
    public SshServer sshServer() throws IOException {
        SshServer sshd = SshServer.setUpDefaultServer();
        sshd.setPort(inboxPort);
        sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider());
        sshd.setUserAuthFactories(Collections.singletonList(new UserAuthPublicKeyFactory()));
        SftpSubsystemFactory sftpSubsystemFactory = new SftpSubsystemFactory();
        sftpSubsystemFactory.addSftpEventListener(sftpEventListener);
        sshd.setSubsystemFactories(Collections.singletonList(sftpSubsystemFactory));
        sshd.setFileSystemFactory(virtualFileSystemFactory());
        sshd.setPasswordAuthenticator(passwordAuthenticator);
        sshd.setPublickeyAuthenticator(publicKeyAuthenticator);
        sshd.start();
        return sshd;
    }

    @Bean
    public VirtualFileSystemFactory virtualFileSystemFactory() {
        return new VirtualFileSystemFactory();
    }

    @Value("${inbox.port}")
    public void setInboxPort(int inboxPort) {
        this.inboxPort = inboxPort;
    }

    @Autowired
    public void setSftpEventListener(SftpEventListener sftpEventListener) {
        this.sftpEventListener = sftpEventListener;
    }

    @Autowired
    public void setPasswordAuthenticator(PasswordAuthenticator passwordAuthenticator) {
        this.passwordAuthenticator = passwordAuthenticator;
    }

    @Autowired
    public void setPublicKeyAuthenticator(PublickeyAuthenticator publicKeyAuthenticator) {
        this.publicKeyAuthenticator = publicKeyAuthenticator;
    }

}
