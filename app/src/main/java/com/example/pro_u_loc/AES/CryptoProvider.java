package com.example.pro_u_loc.AES;

import java.security.Provider;

final class CryptoProvider extends Provider {
    public CryptoProvider(){
        super("Crypto",1.0,"HARMONY (SHA1 digest; SecureRandom; SHA1withDSA signature)");
        put("SecureRandom.SHA1PRNG",
                "org.apache.harmony.security.provider.crypto.SHA1PRNG_SecureRandomImpl");
        put("SecureRandom.SHA1PRNG ImplementedIn","Software");
    }
}
