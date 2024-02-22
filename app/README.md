# Connection Through an SSH Tunnel
This demonstrates a basic username/password connection to a named database via an SSH tunnel.

There are 2 techniques demonstrated here:
1. opening an SSH tunnel and working through it, and
2. connecting to an Oracle database which has a name (not a SID).

Tunneling is not directly related to JDBC, but is a common requirement in real environments. (If you're not familiar with
SSH tunnels, refer to [https://iximiuz.com/en/posts/ssh-tunnels/](https://iximiuz.com/en/posts/ssh-tunnels/), which is the
best explanation I've seen in a while.) The usual architecture is that you first open an SSH session to a bastion host,
then you can open a regular TCP connection to the database host, which is available on the bastion host's secondary network,
but not the network your client is located on (see figure).

... put a figure here ...

This is also a common architecture in cloud environments, where the database host doubles as the bastion host; the public IP
interface is the one you SSH to, but the database is only listening on an interface which is connected internally to the cloud,
and has no external connections.

... put a figure here ...

Implementing a full-feature SSH client in pure Java is a serious undertaking. Fortunately, there are 2 well-known implementations
available -- JSch and the Apache Mina SSHD sub-project. This demonstration uses the Apache implementation.  The relevant code
is in the "Tunnel" class.

The actual JDBC connection is demonstrated in the "OracleDbDriver" class, which retrieves a connection directly.

In order to run this code, copy `configuration.sample.properties` to `configuration.properties`, update to appropriate values
for your situation, and `..\gradlew run`.