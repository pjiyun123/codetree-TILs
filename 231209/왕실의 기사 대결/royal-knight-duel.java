import java.util.*;
import java.io.*;

public class Main {

    static int L; // 체스판 크기
    static int N; // 기사의 수
    static int Q; // 명령의 수
    static int[][] chessMap; // 체스판
    static Knight[] knights; // 기사 정보 
    static int[] initialK; // 초기 체력

    static int[] nr;
    static int[] nc;
    static int[] dmg;
    static boolean[] is_moved;
    
    public static class Knight {
        int r;
        int c;
        int h;
        int w;
        int k;

        public Knight(int r, int c, int h, int w, int k) {
            this.r = r;
            this.c = c;
            this.h = h;
            this.w = w;
            this.k = k;
        }
    } 

    public static void main(String[] args) throws IOException {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");

        L = Integer.parseInt(st.nextToken());
        N = Integer.parseInt(st.nextToken());
        Q = Integer.parseInt(st.nextToken());

        // 체스판 정보 입력 (0 빈칸 / 1 함정 / 2 벽)
        chessMap = new int[L+1][L+1];
        for(int i = 1; i <= L; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            for(int j = 1; j <= L; j++) {
                chessMap[i][j] = Integer.parseInt(st.nextToken());
            }
        }

        // 초기 기사 정보 입력
        knights = new Knight[N+1];
        initialK = new int[N+1];
        for(int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            int h = Integer.parseInt(st.nextToken());
            int w = Integer.parseInt(st.nextToken());
            int k = Integer.parseInt(st.nextToken()); // 초기 체력

            knights[i] = new Knight(r, c, h, w, k);
            initialK[i] = k;
        }

        // 왕의 명령
        nr = new int[N+1];
        nc = new int[N+1];
        dmg = new int[N+1];
        is_moved = new boolean[N+1];
        for(int i = 0; i < Q; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            // i번 기사에게 방향 d로 한 칸 이동하라는 명령
            int idx = Integer.parseInt(st.nextToken());
            int dir = Integer.parseInt(st.nextToken()); // (0 위 / 1 오 / 2 아 / 3 왼)
            if(knights[idx].k > 0)
                knightsMove(idx, dir);
        }

        int totalDamage = 0; // 생존한 기사들이 받은 대미지의 합
        for(int i = 1; i <= N; i++) {
            if(knights[i].k > 0) {
                totalDamage += initialK[i] - knights[i].k;
            }
        }
        System.out.println(totalDamage);

    }

    public static void knightsMove(int idx, int dir) {

        if(tryMovement(idx, dir)) {
            for(int i = 1; i <= N; i++) {
                knights[i].r = nr[i];
                knights[i].c = nc[i];
                knights[i].k -= dmg[i];
            }
        }

    }

    public static boolean tryMovement(int idx, int dir) {

        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, 1, 0, -1};

        Queue<Integer> q = new LinkedList<>();
        boolean is_pos = true;

        for(int i = 1; i <= N; i++) {
            dmg[i] = 0;
            is_moved[i] = false;
            nr[i] = knights[i].r;
            nc[i] = knights[i].c;
        }

        q.add(idx);
        is_moved[idx] = true;

        while(!q.isEmpty()) {
            int x = q.poll();

            nr[x] += dx[dir];
            nc[x] += dy[dir];

            // 경계 벗어나는지 체크 
            if(nr[x] < 1 || nc[x] < 1 || nr[x] + knights[x].h - 1 > L || nc[x] + knights[x].w - 1 > L)
                return false;

            // 대상 조각이 다른 조각이나 장애물과 충돌하는지 검사
            for(int i = nr[x]; i <= nr[x] + knights[x].h - 1; i++) {
                for(int j = nc[x]; j <= nc[x] + knights[x].w - 1; j++) {
                    if(chessMap[i][j] == 1)
                        dmg[x]++;
                    if(chessMap[i][j] == 2)
                        return false;
                }
            }

            // 다른 조각과 충돌하는 경우, 해당 조각도 같이 이동
            for(int i = 1; i <= N; i++) {
                if(is_moved[i] || knights[i].k <= 0)
                    continue;
                if(knights[i].r > nr[x] + knights[x].h - 1 || nr[x] > knights[i].r + knights[i].h - 1)
                    continue;
                if(knights[i].c > nc[x] + knights[x].w - 1 || nc[x] > knights[i].c + knights[i].w - 1)
                    continue;

                is_moved[i] = true;
                q.add(i);
            } 
        }

        dmg[idx] = 0;
        return true;

    }

}