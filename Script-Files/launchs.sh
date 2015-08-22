remoteuser=hbd140030
remotecomputer1=dc35.utdallas.edu
remotecomputer2=dc36.utdallas.edu
remotecomputer3=dc37.utdallas.edu
remotecomputer4=dc38.utdallas.edu
remotecomputer5=dc39.utdallas.edu


ssh -l "$remoteuser" "$remotecomputer1" "cd $HOME;java Node 5" &
ssh -l "$remoteuser" "$remotecomputer2" "cd $HOME;java Node 4" &
ssh -l "$remoteuser" "$remotecomputer3" "cd $HOME;java Node 3" &
ssh -l "$remoteuser" "$remotecomputer4" "cd $HOME;java Node 2" &
ssh -l "$remoteuser" "$remotecomputer5" "cd $HOME;java Node 1" &



#Run this script on CS machine.
#Prerequisite - Passwordless login should be enabled using Public keys and you should have logged on to the net machines atleast once after creating a public key.
#example
#-bash-4.1$ ssh net23.utdallas.edu
#The authenticity of host 'net23.utdallas.edu (10.176.67.86)' can't be established.
#RSA key fingerprint is 66:af:c1:ce:29:b8:5b:7b:8e:25:33:92:bb:96:0e:46.
#Are you sure you want to continue connecting (yes/no)? yes

#Your code should be in directory $HOME/AOS/Project1
#Your main program should be named Project1.java or Project1.cpp or Project1.c
