<p align="center">
  <h1 align="center"> FCL Android</h1>
  <p align="center">
    <i>Connect your dapp to users, their wallets and Flow.</i>
    <br />
    <a href=""><strong>Read the docs»</strong></a>
    <br />
    <br />
    <a href="">Quickstart</a>
    ·
    <a href="https://github.com/Outblock/fcl-android/issues">Report Bug</a>
    ·
    <a href="">Contribute</a>
    ·
    <a href="https://discord.com/channels/911607899795623968/996840274724651028">Discord</a>
 </p>

## Overview
---

This reference documents all the methods available in the SDK, and explains in detail how these methods work. SDKs are open source, and you can use them according to the licence.

----

#### Feature list:
- [x] Sign in/up with Wallet provider
- [x] Configure app
- [x] Query cadence script with arguments
- [x] Send transaction with non-custodial mode (Blocto)
- [x] Send transaction with custodial wallet
- [x] Support all access api endpoint such as `GetAccount` and `GetLastestBlock`

#### Todo list:
- [ ] Sign user message
- [ ] Verify user signature
- [ ] Support custom `authz` function
- [ ] Publish to maven center

## Getting Started

### Installing

Use the configuration below to add this SDK to your project using Maven or Gradle.

#### Gradle

```gradle

allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

```

```gradle

implementation 'com.github.Outblock:fcl-android:0.06'

```

#### Config

Values only need to be set once. We recommend doing this once and as early in the life cycle as possible. To set a configuration value, the `put`
method on the `config` instance needs to be called, the `put` method returns the `config` instance so they can be chained.

```kotlin

Fcl.config(
    appName = "FCLDemo",
    appIcon = "https://placekitten.com/g/200/200",
    location = "https://foo.com",
    env = FlowNetwork.MAINNET,
)

```

### Common Configuration Keys
| Name                            | Example                                              | Description                                                                                                                                                                                    |
| ------------------------------- | ---------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `accessNode` **(required)** | `https://access-testnet.onflow.org`                  | API URL for the Flow Blockchain Access Node you want to be communicating with. See all available access node endpoints [here](https://docs.onflow.org/access-api/#flow-access-node-endpoints). |
| `location` **(required)**       | `https://foo.com`     | Your application's site URL, can be requested by wallets and other services.                                                                                                                |
| `env`                           | `testnet`                                            | Used in conjunction with stored interactions. Possible values: `local`, `canarynet`, `testnet`, `mainnet`                                                                                      |
| `appName`              | `Cryptokitties`                                      | Your applications title, can be requested by wallets and other services.                                                                                                                       |
| `appIcon`               | `https://fcl-discovery.onflow.org/images/blocto.png` | Url for your applications icon, can be requested by wallets and other services.                                                                                                                |

## Wallet Interactions
- These methods allows dapps to interact with FCL compatible wallets in order to authenticate the user and authorize transactions on their behalf.

### `authenticate`
Calling this method will authenticate the current user via any wallet that supports FCL. Once called, FCL will initiate communication with the configured `authn` endpoint which lets the user select a wallet to authenticate with. Once the wallet provider has authenticated the user, FCL will set the values on the [current user](TODO) object for future use and authorization.

```kotlin
Fcl.authenticate(WalletProvider.DAPPER)
```


### `authz`
A **convenience method** that produces the needed authorization details for the current user to submit transactions to Flow. It defines a signing function that connects to a user's wallet provider to produce signatures to submit transactions.

#### Usage

**Note:** The default values for `proposer`, `payer`, and `authorizations` are already `fcl.authz` so there is no need to include these parameters, it is shown only for example purposes. See more on [signing roles](https://docs.onflow.org/concepts/accounts-and-keys/#signing-a-transaction).

```kotlin
val tid = Fcl.mutate {
    cadence(
        """  
        transaction(test: String, testInt: Int) {           
	      prepare(signer: AuthAccount) {                
	          log(signer.address)   
	          log(test)   
	          log(testInt) 
          }
		}
		""".trimIndent()
    )
    arg { string("Test2") }
    arg { int(1) }
    gasLimit(1000)
}
```

## Compatibility
**Minimum Android SDK**: FCL Android requires a minimum API level of 21.

## License
See the [LICENSE](https://github.com/Outblock/fcl-android/blob/main/LICENSE) file for details.
