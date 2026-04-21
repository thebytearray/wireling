# Releasing (maintainers)

Coordinates: **`org.thebytearray.wireguard:WireLing`**. Version comes from [`version.properties`](../version.properties).

In GitHub: **Actions → Release → Run workflow** (patch, minor, or major).

Repository secrets (**Settings → Secrets and variables → Actions**):

| Secret | Required |
|--------|----------|
| `MAVEN_CENTRAL_USERNAME` | Yes |
| `MAVEN_CENTRAL_PASSWORD` | Yes |
| `MAVEN_CENTRAL_GPG_KEY` | Yes |
| `MAVEN_CENTRAL_GPG_PASSWORD` | Only if the GPG key has a passphrase |

Create the Sonatype token per [Central docs](https://central.sonatype.org/publish/generate-portal-token/). GPG: [requirements](https://central.sonatype.org/publish/requirements/gpg/).

If publish fails, open [Deployments](https://central.sonatype.com/publishing/deployments) on the Central Portal.
