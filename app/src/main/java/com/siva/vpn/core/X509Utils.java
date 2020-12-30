package com.siva.vpn.core;

import android.content.Context;
import android.text.TextUtils;

import com.siva.vpn.R;
import com.siva.vpn.handlers.VpnProfile;

import app.openconnect.io.pem.PemObject;
import app.openconnect.io.pem.PemReader;


import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class X509Utils {
	public static Certificate getCertificateFromFile(String certfilename) throws FileNotFoundException, CertificateException {
		CertificateFactory certFact = CertificateFactory.getInstance("X.509");

		InputStream inStream;

		if(certfilename.startsWith(VpnProfile.INLINE_TAG)) {
           int subIndex = certfilename.indexOf("-----BEGIN CERTIFICATE-----");
            subIndex = Math.max(0,subIndex);
			inStream = new ByteArrayInputStream(certfilename.substring(subIndex).getBytes());


        } else {
			inStream = new FileInputStream(certfilename);
        }


		return certFact.generateCertificate(inStream);
	}

	public static PemObject readPemObjectFromFile (String keyfilename) throws IOException {

		Reader inStream;

		if(keyfilename.startsWith(VpnProfile.INLINE_TAG))
			inStream = new StringReader(keyfilename.replace(VpnProfile.INLINE_TAG,""));
		else 
			inStream = new FileReader(new File(keyfilename));

		PemReader pr = new PemReader(inStream);
		PemObject r = pr.readPemObject();
		pr.close();
		return r;
	}




	public static String getCertificateFriendlyName (Context c, String filename) {
		if(!TextUtils.isEmpty(filename)) {
			try {
				X509Certificate cert = (X509Certificate) getCertificateFromFile(filename);

                return getCertificateFriendlyName(cert);

			} catch (Exception e) {
				OpenVPN.logError("Could not read certificate" + e.getLocalizedMessage());
			}
		}
		return c.getString(R.string.cannotparsecert);
	}

    public static String getCertificateFriendlyName(X509Certificate cert) {
        X500Principal principal = cert.getSubjectX500Principal();
        String friendlyName=null;


        if(friendlyName==null)
            friendlyName = principal.getName();

        String[] parts = friendlyName.split(",");
        for (int i=0;i<parts.length;i++){
            String part = parts[i];
            if (part.startsWith("1.2.840.113549.1.9.1=#16")) {
                parts[i] = "email=" + ia5decode(part.replace("1.2.840.113549.1.9.1=#16", ""));
            }
        }
        friendlyName = TextUtils.join(",", parts);
        return friendlyName;
    }

    public static boolean isPrintableChar(char c) {
        Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
        return (!Character.isISOControl(c)) &&
                block != null &&
                block != Character.UnicodeBlock.SPECIALS;
    }

    private static String ia5decode(String ia5string) {
        String d = "";
        for (int i=1;i<ia5string.length();i=i+2) {
            String hexstr = ia5string.substring(i-1,i+1);
            char c = (char) Integer.parseInt(hexstr,16);
            if (isPrintableChar(c)) {
                d+=c;
            } else if (i==1 && (c==0x12 || c==0x1b)) {
            } else {
                d += "\\x" + hexstr;
            }
        }
        return d;
    }
}
