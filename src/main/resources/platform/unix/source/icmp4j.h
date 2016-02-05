//
//  icmp4j.h
//  icmp4j
//
//  Created by laurentb on 12/21/15.
//
// This software is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, similarly
// to how this is described in the GNU Lesser General Public License.

// https://sourceforge.net/projects/icmp4j/

#ifndef icmp4j_h
#define icmp4j_h

#include <stdio.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/in_systm.h>
#include <netinet/ip.h>
#include <netinet/ip_icmp.h>
#include <arpa/inet.h>

#define ERR_MAX_LEN  5 * 1024
#define ICMP4J_VER "1.0.1"

struct Icmp4jStruct {
    char*   host;
    int     ttl;
    int     packetSize;
    long    timeout;
    
    int     retCode;
    int     hasTimeout;
    int     bytes;
    int     returnTtl;
    int     rtt;
    char*   address;
    char*   errorMsg;
    int     errorNo;
};

#define	MAX_DUP_CHK	(8 * 128)
// basically all the global
struct pingStruct {
    __uint16_t seq;
    int options;
    struct sockaddr_in whereto;	/* who to ping */
    int datalen;
    int maxpayload;
    int s;				/* socket file descriptor */
    u_char outpackhdr[IP_MAXPACKET],
        *outpack;
    char DOT;
    char *hostname;
    char *shostname;
    long ident;			/* process id to identify our packets */
    int uid;			/* cached uid for micro-optimization */
    u_char icmp_type;
    u_char icmp_type_rsp;
    int phdr_len;
    int send_len;
    
    /* counters */
    long nreceived;			/* # of packets we got back */
    long ntransmitted;		/* sequence # for outbound packets = #sent */
    
    /* timing */
    int timing;			/* flag to do timing */
} ;

void icmp4j_exist(char** argVersion);
void icmp4j_exist_free(char* argVersion);
void icmp4j_start(struct Icmp4jStruct* icmp4j);
void icmp4j_free(struct Icmp4jStruct* icmp4);
#endif /* icmp4j_h */