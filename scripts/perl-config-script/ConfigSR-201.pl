#!/usr/bin/perl -w
#
# Auteur : Christian DEGUEST
#
# Date : 19/08/2016
#
# But : Modifier la configuration du module SR-201
#
# Remerciements : Urs P. Stettler (https://github.com/cryxli)
#   Donc un grand merci a Urs pour son travail.
#   Globalement ce fichier n'est qu'une remise en forme de tout son travail
#   pour une utilisation en perl.
#   Total respect.
#
#
# Protocole de configuration
#
# Port de connexion : 5111
#
# Dans ce qui suit les XXXX correspondent à un numéro de séquence arbitraire
# Les commandes doivent etre envoyees sans CR ou CR/LF en fin de ligne.
#
# Toutes les commandes commencent par le caractere diese (#) et se finissent
# par le caractere point virgule (;).
#
# Toutes les reponses commencent par le caractere > et se finissent par
# le caractere point virgule (;).
#
# Hormis la commande #1, le module repond :
#   - Si la commande est correcte:
#     >OK;
#
#   - Si la commande est incorrecte:
#     >ERR;
#
# Les modifications ne sont prises en compte qu'apres l'envoi d'une commande
#   #7XXXX;
#
# Pour le fun, vous pouvez utiliser l'utilitaire nc pour dialoguer avec le
# module.
# Attention, pas de CR ni de CR/LF a la fin d'une commande (donc pas de touche
# <return>), mais envoi de la commande par CTRL-D (fin de flux).
#
# Remarque : le module vous deconnecte au bout de 15s sans activite.
#
# exemple :
# $ nc 192.168.0.200 5111
# #11111; (puis CTRL-D)
#
# renvoie:
# >192.168.0.200,255.255.255.0,192.168.0.1,,0,435,F44900BD02CA27000000,192.168.0.1,connect.tutuuu.com,0;
#
#
# #1XXXX;
# -------
# lit la configuraion du module
# Ask the board for its current settings.
#
# Le module renvoit les informations au sous forme d'une chaine de caracteres.
# - La chaine commence par un caractere >.
# - Les parametres sont separes par des virgules (,).
# - la chaine se finit par un point virgule (;)
#
# The board answers with a comma separated list of its settings.
#
# exemple:
#   #11111;
# renvoit
#   >192.168.1.100,255.255.255.0,192.168.1.1,,0,435,F449007D02E2EB000000,192.168.1.1,connect.tutuuu.com,0;
#
# Interpretation:
# 1er parametre   : 192.168.1.100 => adresse IP
# 2eme parametre  : 255.255.255.0 => masque IP
# 3eme parametre  : 192.168.1.1 => passerelle IP
# 4eme parametre  : <rien> => parametre inconnu
# 5eme parametre  : 0 => persistence de l'etat des relais lors d'un redemarrage
# 6eme paramatre  : 435 => version
# 7eme parametre  : F449007D02E2EB000000 => numero de serie (14c) + passwd
# 8eme parametre  : 192.168.1.1 => DNS
# 9eme parametre  : connect.tutuuu.com => serverur Cloud (????)
# 10eme parametre : Etat du mode Cloud (????)
#
# Remarque:
# Toujours envoyer une commande #1XXXX avant de modifier les parametres a l'aide
# d'une autre commande (#2XXXX,aaa.bbb.ccc.ddd; par exemple).
# Dans le cas contraire le module repond >ERR; a toutes vodes demandes de
# modification.
#
#
# #2XXXX,aaa.bbb.ccc.ddd;
# -----------------------
# definit l'adresse IP du module. Attend une adresse IPV4 au format texte.
# Set the board's IP address. Expects a IPv4 as a string (192.168.1.100) as
# argument.
#
# exemple : #21111,192.168.0.200;
#
#
# #3XXXX,aaa.bbb.ccc.ddd;
# -----------------------
# definit le masque IP du module. Attend un masque IPV4 au format texte.
# Set the subnet mask. Expects a IPv4 mask as a string (255.255.255.0) as
# an argument.
#
# exemple : #31111,255.255.255.0;
#
#
# #4XXXX,aaa.bbb.ccc.ddd;
# -----------------------
# definit la passerelle IP. Attend une adresse IPV4 au format texte.
# Set the default gateway used to resolve the cloud service. Expects a IPv4
# as a string (192.168.1.1) as an argument.
#
# exemple : #41111,192.168.0.1;
#
#
# #5XXXX,a;
# ---------
# commande inconnue
# unknown command
#
#
# #6XXXX,a;
# ---------
# valide la perstitence de l'etat des relais lors d'un redemarrage
# Enable persistent relay states when board is powered off and on again.
#
# exemple : #61111,1;
#
#
# #7XXXX;
# -------
# Redemarre le module et donc prend en compte les modifications faites.
# Restart the board. Make changes take effect.
#
# exemple : #71111;
#
#
# #8XXXX,aaa.bbb.ccc.ddd;
# -----------------------
# definit le DNS. Utile pour utiliser le Cloud. Attend une adresse IPV4 au
# format texte.
# Set the DNS server used to resolve the cloud service. Expects a IPv4 as a
# string (192.168.1.1) as an argument.
#
# exemple : #81111,192.168.0.1;
#
#
# #9XXXX,abcdefg...xyz;
# --------------------
# definit le serveur Cloud (???). Attend un nom d'host.
# Set the cloud server host. Expects a host name as an argument.
#
# exemple : #91111,connect.tutuuu.com;
#
#
# #AXXXX,a;
# ---------
# valide l'utilisation du cloud (???)
# Enable or disable the cloud service.
#
# exemple : #A1111,0;
#
#
# #BXXXX,abcdef;
# --------------
# definit le mot de passe pour l'utilisation du cloud (???). Attend un MdP sur
# 6 caracteres.
# Set the password of the cloud service. Expects a 6 character long  password
# as an argument.
#
# exemple : #B1111,0123456;
#
#

use Net::Telnet;
use Term::UI;
use Term::ReadLine;

my $Session = new Net::Telnet(Port => "5111");

my $Term    = Term::ReadLine->new('prompt');

my @Choix = (
   "Lire la configuration",
   "Modifier la configuration",
   "Quitter");

my $Saisie;
my $Reponse;
my $AdresseIP;
my $MasqueIP;
my $Passerelle;
my $Persistenec;
my $DNS;

do {
   $Saisie = $Term->get_reply(
      prompt  => 'Choisir un nombre : ',
      choices => \@Choix,
      default => $Choix[2],
   );

   if($Saisie  eq $Choix[0]) {
      $Session->open($ARGV[0]);

      # Lecture des parametres actuelles
      my $Commande = "#11111;";
      $Session->put($Commande);
      $Reponse = $Session->get();

      $Session->close();

#               $Commande = "#41111,192.168.0.1;";
#               $Session->put($Commande);
#               $Reponse = $Session->get();
#      print $ Reponse;
      if($Reponse eq ">ERR;") {
         print("Erreur\n");
      } else {
         @Parametres = split(/,/,$Reponse);

         $AdresseIP = substr($Parametres[0],1,15);;
         $MasqueIP = $Parametres[1];
         $Passerelle = $Parametres[2];
         $Persistence = $Parametres[4];
         $DNS = $Parametres[7];

         print "\n";
         print "Parametres:\n";
         print "Adresse Ip  : ".$AdresseIP."\n";
         print "Masque Ip   : ".$MasqueIP."\n";
         print "Passerelle  : ".$Passerelle."\n";
         print "Persistence : ".$Persistence."\n";
         print "DNS         : ".$DNS."\n";
         print "\n";
      }
   } elsif ($Saisie  eq $Choix[1]) {
      $Session->open($ARGV[0]);

      # Lecture des parametres actuelles
      my $Commande = "#11111;";
      $Session->put($Commande);
      $Reponse = $Session->get();

      $Session->close();

#               $Commande = "#41111,192.168.0.1;";
#               $Session->put($Commande);
#               $Reponse = $Session->get();
#      print $ Reponse;
      if($Reponse eq ">ERR;") {
         print("Erreur de lecture des parametres\n");
      } else {
         @Parametres = split(/,/,$Reponse);

         $AdresseIP = substr($Parametres[0],1,15);;
         $MasqueIP = $Parametres[1];
         $Passerelle = $Parametres[2];
         $Persistence = $Parametres[4];
         $DNS = $Parametres[7];

         print "\n";
         print "Parametres:\n";
         $AdresseIP = $Term->get_reply(
            prompt  => "Adresse Ip",
            default => $AdresseIP);
         $MasqueIP = $Term->get_reply(
            prompt  => "Masque Ip",
            default => $MasqueIP);
         $Passerelle = $Term->get_reply(
            prompt  => "Passerelle",
            default => $Passerelle);
         $Persistence = $Term->get_reply(
            prompt  => "Persistence",
            default => $Persistence);
         $DNS = $Term->get_reply(
            prompt  => "DNS",
            default => $DNS);
         print "\n";

         my $OuiNon = $Term->ask_yn(
            prompt => "Ecrire ces valeurs et redemarrer le module?",
            default => 'n',
         );
         if($OuiNon) {
            $Session->open($ARGV[0]);

            # Lecture des parametres actuelles (obligatoire au cause du timout
            # de 15 secondes
            my $Commande = "#11111;";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur de lecture des parametres\n");
            }

            # adresse IP
            $Commande = "#21111,".$AdresseIP.";";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur d'ecriture de l'adresse IP\n");
            }

            # Masque IP
            $Commande = "#31111,".$MasqueIP.";";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur d'ecriture du masque IP\n");
            }

            # Passerelle
            $Commande = "#41111,".$Passerelle.";";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur d'ecriture de la passerelle\n");
            }
            # Persistence
            $Commande = "#61111,".$Persistence.";";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur d'ecriture de la persitence\n");
            }

            # DNS
            $Commande = "#81111,".$DNS.";";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur d'ecriture du DNS\n");
            }


            $Commande = "#71111;";
            $Session->put($Commande);
            $Reponse = $Session->get();

            if($Reponse eq ">ERR;") {
               print("Erreur de prise en compte des parametres");
            }
            $Session->close();
         }
      }
   }
}
while($Saisie ne $Choix[2]);


