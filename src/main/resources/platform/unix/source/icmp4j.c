//
//  icmp4j.c
//  icmp4j
//
//  Created by laurentb on 12/21/15.
//
// This code is derivated from ping.c developped at Berkeley.
// It provided an ICMP ECHO facility as a dynamic library to be
// called from a java program

// This software is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, similarly
// to how this is described in the GNU Lesser General Public License.
// https://sourceforge.net/projects/icmp4j/

#include "icmp4j.h"

#include <stdio.h>

/*
 * Copyright (c) 1989, 1993
 *	The Regents of the University of California.  All rights reserved.
 *
 * This code is derived from software contributed to Berkeley by
 * Mike Muuss.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 4. Neither the name of the University nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

#if 0
#ifndef lint
static const char copyright[] =
"@(#) Copyright (c) 1989, 1993\n\
The Regents of the University of California.  All rights reserved.\n";
#endif /* not lint */

#ifndef lint
static char sccsid[] = "@(#)ping.c	8.1 (Berkeley) 6/5/93";
#endif /* not lint */
#endif
#include <sys/cdefs.h>

/*
 *			P I N G . C
 *
 * Using the Internet Control Message Protocol (ICMP) "ECHO" facility,
 * measure round-trip-delays and packet loss across network paths.
 *
 * Author -
 *	Mike Muuss
 *	U. S. Army Ballistic Research Laboratory
 *	December, 1983
 *
 * Status -
 *	Public Domain.  Distribution Unlimited.
 * Bugs -
 *	More statistics could always be gathered.
 *	This program has to run SUID to ROOT to access the ICMP socket.
 */

#include <sys/param.h>		/* NB: we rely on this for <sys/types.h> */
#include <sys/socket.h>
#include <sys/sysctl.h>
#include <sys/time.h>
#include <sys/uio.h>

#include <netinet/in.h>
#include <netinet/in_systm.h>
#include <netinet/ip.h>
#include <netinet/ip_icmp.h>
#include <arpa/inet.h>

#ifdef IPSEC
#include <netinet6/ipsec.h>
#endif /*IPSEC*/

#include <ctype.h>
#include <err.h>
#include <errno.h>
#include <math.h>
#include <netdb.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sysexits.h>
#include <unistd.h>

#include <time.h>
#include <pthread.h>


#define	INADDR_LEN	((int)sizeof(in_addr_t))
#define	TIMEVAL_LEN	((int)sizeof(struct timeval))
#define	MASK_LEN	(ICMP_MASKLEN - ICMP_MINLEN)
#define	TS_LEN		(ICMP_TSLEN - ICMP_MINLEN)
#define	DEFDATALEN	56		/* default data length */
#define	FLOOD_BACKOFF	20000		/* usecs to back off if F_FLOOD mode */
/* runs out of buffer space */
#define	MAXIPLEN	(sizeof(struct ip) + MAX_IPOPTLEN)
#define	MAXICMPLEN	(ICMP_ADVLENMIN + MAX_IPOPTLEN)
#define	MAXWAIT		10		/* max seconds to wait for response */
#define	MAXALARM	(60 * 60)	/* max seconds for alarm timeout */
#define	MAXTOS		255


#define	F_TTL		0x8000
#define	F_TIME		0x100000

#define	MAX_IPOPTLEN	40 // from netinet/ip_var.h

static u_short in_cksum(u_short *, int);
static void finish(struct Icmp4jStruct*, const struct pingStruct );
static void pinger(struct pingStruct *);
static void pr_icmph(struct Icmp4jStruct * , struct icmp *);
static void pr_pack(char *, ssize_t, struct sockaddr_in *, struct timeval *, struct Icmp4jStruct *,  struct pingStruct *);
static void tvsub(struct timeval *, struct timeval *);
static void debugStr(char* arg);


pthread_mutex_t count_mutex;
__uint16_t gSequence = 1;

static __uint16_t getSequence() {
    pthread_mutex_lock(&count_mutex);
    gSequence = gSequence + 1;
    if (gSequence == 0) {
        gSequence = 1;
    }
    pthread_mutex_unlock(&count_mutex);
    return gSequence;
}

void icmp4j_exist(char** argVersion) {
    *argVersion = (char*)malloc(sizeof(char) * (1+strlen(ICMP4J_VER)));
    memset(*argVersion, 0, sizeof(char) * 6);
    strcpy(*argVersion, ICMP4J_VER);
}
void icmp4j_exist_free(char* argVersion) {
    free(argVersion);
}


void icmp4j_free(struct Icmp4jStruct* icmp4j) {
    if (icmp4j->errorMsg != NULL) {
       free(icmp4j->errorMsg);
    }
    if (icmp4j->address != NULL) {
       free(icmp4j->address);
    }
}

static void initPing(struct pingStruct *pingData) {
    pingData->options = 0;
    pingData->datalen = DEFDATALEN;
    pingData->DOT = '.';
    pingData->icmp_type = ICMP_ECHO;
    pingData->icmp_type_rsp = ICMP_ECHOREPLY;
    pingData->phdr_len = 0;
    pingData->nreceived = pingData->ntransmitted = 0;
    pingData->seq = getSequence();
}
/*
 * finish --
 *	Print out statistics, and give up.
 */
static void finish(struct Icmp4jStruct *icmp4jPtr, const struct pingStruct pingData) {
    int tmpErrno;
    //printf("finish closing socket = %d\n" , arg.s);
    //fflush(stdout);
    if ( pingData.s != -1 && close(pingData.s) != 0) {
        tmpErrno = errno;
    }
    //finish_up = 0;
    if (pingData.nreceived)
        icmp4jPtr->retCode = 1;
    else
        icmp4jPtr->retCode = 0;
}


void icmp4j_start(struct Icmp4jStruct* icmp4j) {
    struct pingStruct pingData;
    struct sockaddr_in from;
    struct iovec iov;
    struct msghdr msg;
    u_char packet[IP_MAXPACKET];
    char *source, *target;
    struct hostent *hp;
#ifdef IPSEC_POLICY_IPSEC
    char *policy_in, *policy_out;
#endif
    struct sockaddr_in *to;
    int almost_done, df, hold, icmp_len, preload, tos, ttl;
    char ctrl[CMSG_SPACE(sizeof(struct timeval))];
    char hnamebuf[MAXHOSTNAMELEN];
    
    source = NULL;
    struct timeval sockettimeout;
#ifdef IPSEC_POLICY_IPSEC
    policy_in = policy_out = NULL;
#endif
    
    
    icmp4j->retCode = 1;
    icmp4j->errorNo = EX_OK;
    icmp4j->hasTimeout = 0;
    icmp4j->errorMsg = (char*) malloc(sizeof(char) * ERR_MAX_LEN + 1);
    icmp4j->address = NULL;
    icmp4j->returnTtl = 0;
    icmp4j->rtt = 0;
    icmp4j->bytes = 0;
    
    initPing(&pingData);
    /*
     * Do the stuff that we need root priv's for *first*, and
     * then drop our setuid bit.  Save error reporting for
     * after arg parsing.
     */
    
    // osx (and BSD kernel I guess) allows to open ICMP socket without root priv
    // but we are not using it
    //    if (getuid())
    //        s = socket(AF_INET, SOCK_DGRAM, IPPROTO_ICMP);
    //    else
    //
    pingData.s = socket(AF_INET, SOCK_RAW, IPPROTO_ICMP);
    icmp4j->errorNo = errno;
    
    if (pingData.s == -1) {
        sprintf(icmp4j->errorMsg, "cannot create socket: %s", strerror(icmp4j->errorNo));
        icmp4j->retCode = 0;
        finish(icmp4j, pingData);
        return;
    }
    setuid(getuid());
    //uid = getuid();

/*    printf("%p self = %p  seq= %u\n", &pingData, pthread_self(), pingData.seq);*/
/*    fflush(stdout);*/

    
    pingData.ident = getpid() & 0xFFFF;
    df = preload = tos = 0;
    
    target = icmp4j->host;
    pingData.outpack = pingData.outpackhdr + sizeof(struct ip);
//    pingData.npackets = 1;
    if (icmp4j->ttl != 0) {
        ttl = icmp4j->ttl;
        pingData.options |= F_TTL;
    }
    if (icmp4j->packetSize != 0) {
        pingData.datalen = icmp4j->packetSize;
    }
    sockettimeout.tv_sec = sockettimeout.tv_usec = 0;
    if (icmp4j->timeout > 0) {
        sockettimeout.tv_sec = icmp4j->timeout / 1000;
        sockettimeout.tv_usec = (int)(icmp4j->timeout % 1000) * 10;
    }
    
    icmp_len = sizeof(struct ip) + ICMP_MINLEN + pingData.phdr_len;
    pingData.maxpayload = IP_MAXPACKET - icmp_len;
    if (pingData.datalen > pingData.maxpayload) {
        icmp4j->errorNo = EX_NOHOST;
        sprintf(icmp4j->errorMsg, "packet size too large: %d > %d", pingData.datalen, pingData.maxpayload);
        finish(icmp4j, pingData);
        return;
    }
    
    pingData.send_len = icmp_len + pingData.datalen;
    
    
    bzero(&pingData.whereto, sizeof(pingData.whereto));
    to = &pingData.whereto;
    to->sin_family = AF_INET;
    // commented for Linux compatibility, not sure why !!
    //to->sin_len = sizeof *to;
    if (inet_aton(target, &to->sin_addr) != 0) {
        pingData.hostname = target;
    } else {
        hp = gethostbyname2(target, AF_INET);
        if (!hp) {
            icmp4j->errorNo = EX_NOHOST;
            sprintf(icmp4j->errorMsg, "cannot resolve %s: %s", target, hstrerror(h_errno));
            finish(icmp4j, pingData);
            return;
        }
        if ((unsigned)hp->h_length > sizeof(to->sin_addr)){
            icmp4j->errorNo = EX_NOHOST;
            sprintf(icmp4j->errorMsg, "gethostbyname2 returned an illegal address");
            finish(icmp4j, pingData);
            return;
        }
        memcpy(&to->sin_addr, hp->h_addr_list[0], sizeof to->sin_addr);
        (void)strncpy(hnamebuf, hp->h_name, sizeof(hnamebuf) - 1);
        hnamebuf[sizeof(hnamebuf) - 1] = '\0';
        pingData.hostname = hnamebuf;
    }
    hold = 1;
    if (pingData.options & F_TTL) {
        if (setsockopt(pingData.s, IPPROTO_IP, IP_TTL, &ttl, sizeof(ttl)) < 0) {
            icmp4j->errorNo = EX_OSERR;
            sprintf(icmp4j->errorMsg, "setsockopt IP_TTL");
            finish(icmp4j, pingData);
            return;
        }
    }
    
    /*
     * When pinging the broadcast address, you can get a lot of answers.
     * Doing something so evil is useful if you are trying to stress the
     * ethernet, or just want to fill the arp cache to get some stuff for
     * /etc/ethers.  But beware: RFC 1122 allows hosts to ignore broadcast
     * or multicast pings if they wish.
     */
    
    /*
     * XXX receive buffer needs undetermined space for mbuf overhead
     * as well.
     */
    hold = IP_MAXPACKET + 128;
    (void)setsockopt(pingData.s, SOL_SOCKET, SO_RCVBUF, (char *)&hold, sizeof(hold));
    (void)setsockopt(pingData.s, SOL_SOCKET, SO_SNDBUF, (char *)&hold, sizeof(hold));
    
    if (pingData.datalen >= TIMEVAL_LEN)	/* can we time transfer */
        pingData.timing = 1;
    
    bzero(&msg, sizeof(msg));
    msg.msg_name = (caddr_t)&from;
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;
#ifdef SO_TIMESTAMP
    msg.msg_control = (caddr_t)ctrl;
#endif
    iov.iov_base = packet;
    iov.iov_len = IP_MAXPACKET;
    
    pinger(&pingData);		/* send the first ping */
    almost_done = 0;
    while (!almost_done) {
        struct timeval now;
        fd_set rfds;
        int n;
        ssize_t cc;
        
        if ((unsigned)pingData.s >= FD_SETSIZE) {
            icmp4j->errorNo = EX_OSERR;
            sprintf(icmp4j->errorMsg, "descriptor too large");
            finish(icmp4j, pingData);
            return;
        }
        FD_ZERO(&rfds);
        FD_SET(pingData.s, &rfds);
        n = select(pingData.s + 1, &rfds, NULL, NULL, &sockettimeout);
        if (n < 0) {
            if (errno != EINTR ) {
                icmp4j->errorNo = errno;
                sprintf(icmp4j->errorMsg, "error on select %s [%d]", strerror(icmp4j->errorNo), icmp4j->errorNo);
                icmp4j->retCode = 0;
                finish(icmp4j, pingData);
                return;
            }
       }
       if (n == 0) {
            icmp4j->hasTimeout = 1;
            sprintf(icmp4j->errorMsg, "Request timeout");
            finish(icmp4j, pingData);
/*            printf(" --> timeout at %p %ld [%p]\n", &pingData, pingData.nreceived, icmp4j);*/
/*            fflush(stdout);*/
            return;
        }
        if (n == 1) {
#ifdef SO_TIMESTAMP
            msg.msg_controllen = sizeof(ctrl);
#endif
            msg.msg_namelen = sizeof(from);
            if ((cc = recvmsg(pingData.s, &msg, 0)) < 0) {
                icmp4j->errorNo = errno;
                sprintf(icmp4j->errorMsg, "error on recvmsg %s [%d]", strerror(icmp4j->errorNo), icmp4j->errorNo);
                icmp4j->retCode = 0;
                finish(icmp4j, pingData);
                return;
            }
            (void)gettimeofday(&now, NULL);
            pr_pack((char *)packet, cc, &from, &now, icmp4j, &pingData);
            almost_done = (pingData.nreceived ==1);
        }
    }
    finish(icmp4j, pingData);
    return;
}



/*
 * pinger --
 *	Compose and transmit an ICMP ECHO REQUEST packet.  The IP packet
 * will be added on by the kernel.  The ID field is our UNIX process ID,
 * and the sequence number is an ascending integer.  The first TIMEVAL_LEN
 * bytes of the data portion are used to hold a UNIX "timeval" struct in
 * host byte-order, to compute the round-trip time.
 */
static void pinger(struct pingStruct *pingData) {
    struct timeval now;
    struct icmp *icp;
    int cc;
    u_char *packet;
    
    packet = pingData->outpack;
    icp = (struct icmp *)pingData->outpack;
    icp->icmp_type = pingData->icmp_type;
    icp->icmp_code = 0;
    icp->icmp_cksum = 0;
    icp->icmp_seq = pingData->seq; //htons(pingData->ntransmitted);
    icp->icmp_id = pingData->ident;			/* ID */
    
    if ((pingData->options & F_TIME) || pingData->timing) {
        (void)gettimeofday(&now, NULL);
        if (pingData->options & F_TIME)
            icp->icmp_otime = htonl((now.tv_sec % (24*60*60)) * 1000 + now.tv_usec / 1000);
        if (pingData->timing)
            bcopy((void *)&now,
                  (void *)&pingData->outpack[ICMP_MINLEN + pingData->phdr_len],
                  sizeof(struct timeval));
    }
    
    cc = ICMP_MINLEN + pingData->phdr_len + pingData->datalen;
    /* compute ICMP checksum here */
    icp->icmp_cksum = in_cksum((u_short *)icp, cc);
    
    sendto(pingData->s, (char *)packet, cc, 0, (struct sockaddr *)&pingData->whereto, sizeof(pingData->whereto));
    pingData->ntransmitted++;
}

/*
 * pr_pack --
 *	Print out the packet, if it came from us.  This logic is necessary
 * because ALL readers of the ICMP socket get a copy of ALL ICMP packets
 * which arrive ('tis only fair).  This permits multiple copies of this
 * program to be run without having intermingled output (or statistics!).
 */
static void pr_pack(char *buf, ssize_t cc, struct sockaddr_in *from, struct timeval *tv, struct Icmp4jStruct *icmp4jPtr, struct pingStruct *pingData) {
    struct icmp *icp;
    struct ip *ip;
    const void *tp;
    double triptime;
    ssize_t hlen, recv_len;
    int seq;
    char tempAd[500];
    
    /* Check the IP header */
    ip = (struct ip *)buf;
    hlen = ip->ip_hl << 2;
    recv_len = cc;
    if (cc < hlen + ICMP_MINLEN) {
        //warn("packet too short (%d bytes) from %s", cc, inet_ntoa(from->sin_addr));
        return;
    }
    
    
    /* Now the ICMP part */
    cc -= hlen;
    icp = (struct icmp *)(buf + hlen);
    if (icp->icmp_type == pingData->icmp_type_rsp) {
        if ((icp->icmp_seq != pingData->seq) || (icp->icmp_id != pingData->ident)) {
            return;			/* 'Twas not our ECHO */
        }
        ++pingData->nreceived;
        triptime = 0.0;
        if (pingData->timing) {
            struct timeval tv1;
#ifndef icmp_data
            tp = &icp->icmp_ip;
#else
            tp = icp->icmp_data;
#endif
            tp = (const char *)tp + pingData->phdr_len;
            
            if (cc - ICMP_MINLEN - pingData->phdr_len >= sizeof(tv1)) {
                /* Copy to avoid alignment problems: */
                memcpy(&tv1, tp, sizeof(tv1));
                tvsub(tv, &tv1);
                triptime = ((double)tv->tv_sec) * 1000.0 + ((double)tv->tv_usec) / 1000.0;
            } else
                pingData->timing = 0;
        }
        seq = ntohs(icp->icmp_seq);
        
        
        icmp4jPtr->address = (char*) malloc(sizeof(char) * strlen(inet_ntoa(*(struct in_addr *)&from->sin_addr.s_addr)) + 1);
        strcpy(icmp4jPtr->address, inet_ntoa(*(struct in_addr *)&from->sin_addr.s_addr));

        icmp4jPtr->returnTtl = ip->ip_ttl;
        icmp4jPtr->rtt = triptime;
        
        // casting shouldn't be a big deal
        icmp4jPtr->bytes = (int)cc;
    } else {
        /*
         * We've got something other than an ECHOREPLY.
         * See if it's a reply to something that we sent.
         * We can compare IP destination, protocol,
         * and ICMP type and ID.
         *
         * Only print all the error messages if we are running
         * as root to avoid leaking information not normally
         * available to those not running as root.
         */
#ifndef icmp_data
        struct ip *oip = &icp->icmp_ip;
#else
        struct ip *oip = (struct ip *)icp->icmp_data;
#endif
        struct icmp *oicmp = (struct icmp *)(oip + 1);
        
        if ( (oip->ip_dst.s_addr == pingData->whereto.sin_addr.s_addr) &&
            (oip->ip_p == IPPROTO_ICMP) &&
            (oicmp->icmp_type == ICMP_ECHO) &&
            (oicmp->icmp_id == pingData->ident)) {
            pr_icmph(icmp4jPtr, icp);
        }
        return;
    }
}

/*
 * in_cksum --
 *	Checksum routine for Internet Protocol family headers (C Version)
 */
u_short in_cksum(u_short *addr, int len) {
    int nleft, sum;
    u_short *w;
    union {
        u_short	us;
        u_char	uc[2];
    } last;
    u_short answer;
    
    nleft = len;
    sum = 0;
    w = addr;
    
    /*
     * Our algorithm is simple, using a 32 bit accumulator (sum), we add
     * sequential 16 bit words to it, and at the end, fold back all the
     * carry bits from the top 16 bits into the lower 16 bits.
     */
    while (nleft > 1)  {
        sum += *w++;
        nleft -= 2;
    }
    
    /* mop up an odd byte, if necessary */
    if (nleft == 1) {
        last.uc[0] = *(u_char *)w;
        last.uc[1] = 0;
        sum += last.us;
    }
    
    /* add back carry outs from top 16 bits to low 16 bits */
    sum = (sum >> 16) + (sum & 0xffff);	/* add hi 16 to low 16 */
    sum += (sum >> 16);			/* add carry */
    answer = ~sum;				/* truncate to 16 bits */
    return(answer);
}

/*
 * tvsub --
 *	Subtract 2 timeval structs:  out = out - in.  Out is assumed to
 * be >= in.
 */
static void tvsub(struct timeval *out, struct timeval *in) {
    if ((out->tv_usec -= in->tv_usec) < 0) {
        --out->tv_sec;
        out->tv_usec += 1000000;
    }
    out->tv_sec -= in->tv_sec;
}

/*
 * pr_icmph --
 *	Print a descriptive string about an ICMP header.
 */
static void pr_icmph(struct Icmp4jStruct *icmp4jPtr, struct icmp *icp) {
    icmp4jPtr->errorNo = icp->icmp_type;
    switch(icp->icmp_type) {
        case ICMP_ECHOREPLY:
            sprintf(icmp4jPtr->errorMsg, "Echo Reply");
            /* XXX ID + Seq + Data */
            break;
        case ICMP_UNREACH:
            switch(icp->icmp_code) {
                case ICMP_UNREACH_NET:
                    sprintf(icmp4jPtr->errorMsg, "Destination Net Unreachable");
                    break;
                case ICMP_UNREACH_HOST:
                    sprintf(icmp4jPtr->errorMsg, "Destination Host Unreachable");
                    break;
                case ICMP_UNREACH_PROTOCOL:
                    sprintf(icmp4jPtr->errorMsg, "Destination Protocol Unreachable");
                    break;
                case ICMP_UNREACH_PORT:
                    sprintf(icmp4jPtr->errorMsg, "Destination Port Unreachable");
                    break;
                case ICMP_UNREACH_NEEDFRAG:
                    sprintf(icmp4jPtr->errorMsg, "frag needed and DF set (MTU %d)", ntohs(icp->icmp_nextmtu));
                    break;
                case ICMP_UNREACH_SRCFAIL:
                    sprintf(icmp4jPtr->errorMsg, "Source Route Failed");
                    break;
                case ICMP_UNREACH_FILTER_PROHIB:
                    sprintf(icmp4jPtr->errorMsg, "Communication prohibited by filter");
                    break;
                default:
                    sprintf(icmp4jPtr->errorMsg, "Dest Unreachable, Bad Code: %d", icp->icmp_code);
                    break;
            }
            break;
        case ICMP_SOURCEQUENCH:
            sprintf(icmp4jPtr->errorMsg, "Source Quench");
            break;
        case ICMP_REDIRECT:
            switch(icp->icmp_code) {
                case ICMP_REDIRECT_NET:
                    sprintf(icmp4jPtr->errorMsg, "Redirect Network (New addr: %s)", inet_ntoa(icp->icmp_gwaddr));
                    break;
                case ICMP_REDIRECT_HOST:
                    sprintf(icmp4jPtr->errorMsg, "Redirect Host (New addr: %s)", inet_ntoa(icp->icmp_gwaddr));
                    break;
                case ICMP_REDIRECT_TOSNET:
                    sprintf(icmp4jPtr->errorMsg, "Redirect Type of Service and Network (New addr: %s)", inet_ntoa(icp->icmp_gwaddr));
                    break;
                case ICMP_REDIRECT_TOSHOST:
                    sprintf(icmp4jPtr->errorMsg, "Redirect Type of Service and Host (New addr: %s)", inet_ntoa(icp->icmp_gwaddr));
                    break;
                default:
                    sprintf(icmp4jPtr->errorMsg, "Redirect, Bad Code: %d", icp->icmp_code);
                    break;
            }
            break;
        case ICMP_ECHO:
            sprintf(icmp4jPtr->errorMsg, "Echo Request");
            /* XXX ID + Seq + Data */
            break;
        case ICMP_TIMXCEED:
            switch(icp->icmp_code) {
                case ICMP_TIMXCEED_INTRANS:
                    sprintf(icmp4jPtr->errorMsg, "Time to live exceeded");
                    break;
                case ICMP_TIMXCEED_REASS:
                    sprintf(icmp4jPtr->errorMsg, "Frag reassembly time exceeded");
                    break;
                default:
                    sprintf(icmp4jPtr->errorMsg, "Time exceeded, Bad Code: %d", icp->icmp_code);
                    break;
            }
            break;
        case ICMP_PARAMPROB:
            sprintf(icmp4jPtr->errorMsg, "Parameter problem: pointer = 0x%02x", icp->icmp_hun.ih_pptr);
            break;
        case ICMP_TSTAMP:
            sprintf(icmp4jPtr->errorMsg, "Timestamp");
            /* XXX ID + Seq + 3 timestamps */
            break;
        case ICMP_TSTAMPREPLY:
            sprintf(icmp4jPtr->errorMsg, "Timestamp Reply");
            /* XXX ID + Seq + 3 timestamps */
            break;
        case ICMP_IREQ:
            sprintf(icmp4jPtr->errorMsg, "Information Request");
            /* XXX ID + Seq */
            break;
        case ICMP_IREQREPLY:
            sprintf(icmp4jPtr->errorMsg, "Information Reply");
            /* XXX ID + Seq */
            break;
        case ICMP_MASKREQ:
            sprintf(icmp4jPtr->errorMsg, "Address Mask Request");
            break;
        case ICMP_MASKREPLY:
            sprintf(icmp4jPtr->errorMsg, "Address Mask Reply");
            break;
        case ICMP_ROUTERADVERT:
            sprintf(icmp4jPtr->errorMsg, "Router Advertisement");
            break;
        case ICMP_ROUTERSOLICIT:
            sprintf(icmp4jPtr->errorMsg, "Router Solicitation");
            break;
        default:
            sprintf(icmp4jPtr->errorMsg, "Bad ICMP type: %d", icp->icmp_type);
    }
}
