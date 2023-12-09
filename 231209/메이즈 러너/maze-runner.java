import java.util.*;
import java.io.*;

public class Main {

    static int N; // 미로의 크기
    static int M; // 참가자 수
    static int K; // 게임 시간
    static int[][] miro; // 미로
    static Loc[] gamer; // 참가자 위치
    static Loc exit; // 출구 위치 
    static boolean[] gameover; // 참가자 탈출 여부
    static int gameoverCnt = 0; // 탈출한 참가자 수
    static int sx;
    static int sy;
    static int squareSize;
    static int totalDist = 0; // 모든 참가자들의 이동 거리 합

    public static class Loc {
        int r;
        int c;

        public Loc(int r, int c) {
            this.r = r;
            this.c = c;
        }
    }

    public static void main(String[] args) throws IOException{
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        StringTokenizer st = new StringTokenizer(br.readLine(), " ");

        N = Integer.parseInt(st.nextToken());
        M = Integer.parseInt(st.nextToken());
        K = Integer.parseInt(st.nextToken());

        miro = new int[N+1][N+1];
        for(int i = 1; i <= N; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            for(int j = 1; j <= N; j++) {
                miro[i][j] = Integer.parseInt(st.nextToken()); // (0 빈칸 / 1~9 벽)
            }
        }  

        gamer = new Loc[M];
        gameover = new boolean[M];
        for(int i = 0; i < M; i++) {
            st = new StringTokenizer(br.readLine(), " ");
            int r = Integer.parseInt(st.nextToken());
            int c = Integer.parseInt(st.nextToken());
            gamer[i] = new Loc(r, c);
        }

        st = new StringTokenizer(br.readLine(), " ");
        exit = new Loc(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));

        while(K-- > 0) {
            // 참가자 이동
            for(int i = 0; i < M; i++) {
                if(!gameover[i])
                    moveGamer(i);
            }

            // 모든 참가자가 탈출했다면 종료
            if(gameoverCnt == M)
                break;

            // 가장 작은 정사각형 찾기
            findMinimumSquare();

            // 정사각형 회전하기
            rotateSquare();

            // 참가자와 출구 회전하기
            rotateGamerAndExit();
        }

        System.out.println(totalDist);
        System.out.println(exit.r + " " + exit.c);

    }

    public static void moveGamer(int idx) {

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        int r = gamer[idx].r;
        int c = gamer[idx].c;
        int dist = Math.abs(r-exit.r) + Math.abs(c-exit.c);

        int goalR = r;
        int goalC = c;
        boolean flag = false;

        for(int i = 0; i < 4; i++) {
            int nextR = r + dr[i];
            int nextC = c + dc[i];

            if(nextR < 1 || nextR > N || nextC < 1 || nextC > N)
                continue;
            
            if(miro[nextR][nextC] != 0)
                continue;
            
            int nextDist = Math.abs(nextR-exit.r) + Math.abs(nextC-exit.c);
            if(nextDist < dist) {
                dist = nextDist;
                goalR = nextR;
                goalC = nextC;
                flag = true;
                break;
            }
        }

        gamer[idx].r = goalR;
        gamer[idx].c = goalC;
        if(flag) totalDist++;

        if(gamer[idx].r == exit.r && gamer[idx].c == exit.c) {
            gameover[idx] = true;
        }

    }

    public static void findMinimumSquare() {
        // 가장 작은 정사각형부터 모든 정사각형을 만들어본다
        for(int sz = 2; sz <= N; sz++) {
            for(int x1 = 1; x1 <= N - sz + 1; x1++) {
                for(int y1 = 1; y1 <= N - sz + 1; y1++) {
                    int x2 = x1 + sz - 1;
                    int y2 = y1 + sz - 1;

                    // 만약 출구가 해당 정사각형에 없다면 스킵 
                    if(!(x1 <= exit.r && exit.r <= x2 && y1 <= exit.c && exit.c <= y2))
                        continue;
                    
                    // 한 명 이상의 참가자가 해당 정사각형 안에 있는지 판단
                    boolean isIn = false;
                    for(int i = 0; i < M; i++) {
                        if(x1 <= gamer[i].r && gamer[i].r <= x2 && y1 <= gamer[i].c && gamer[i].c <= y2 && !gameover[i])
                            isIn = true;
                    }

                    // 만약 한 명 이상의 참가자가 해당 정사각형 안에 있다면
                    if(isIn) {
                        sx = x1;
                        sy = y1;
                        squareSize = sz;
                        return;
                    }
                }
            }
        }
    }

    public static void rotateSquare() {

        int[][] square = new int[N+1][N+1];

        // 정사각형 안에 있는 벽돌의 내구성 1 감소시키기
        for(int i = sx; i < sx + squareSize; i++) {
            for(int j = sy; j < sy + squareSize; j++) {
                if(miro[i][j] > 0)
                    miro[i][j]--;
            }
        }

        // 정사각형을 시계방향으로 90도 회전하기
        for(int x = sx; x < sx + squareSize; x++) {
            for(int y = sy; y < sy + squareSize; y++) {
                // (sx, sy)를 (0, 0)으로 옮겨주는 변환 진행
                int ox = x - sx;
                int oy = y - sy;
                // 변환된 상태에서는 회전 이후 좌표가 (x, y) -> (y, squareSize-x-1)
                int rx = oy;
                int ry = squareSize - ox - 1;
                // 다시 (sx, sy)를 더해주기
                square[rx+sx][ry+sy] = miro[x][y];
            }
        }

        // square값을 현재 miro에 옮겨주기
        for(int x = sx; x < sx + squareSize; x++) {
            for(int y = sy; y < sy + squareSize; y++) {
                miro[x][y] = square[x][y];
            }
        }

    }

    public static void rotateGamerAndExit() {

        // M명의 참가자 확인 후 회전
        for(int i = 0; i < M; i++) {
            int x = gamer[i].r;
            int y = gamer[i].c;
            // 해당 참가자가 정사각형 안에 포함되어 있을 때만 회전 시키기
            if(sx <= x && x < sx + squareSize && sy <= y && y < sy + squareSize) {
                int ox = x - sx;
                int oy = y - sy;
                int rx = oy;
                int ry = squareSize - ox - 1;
                gamer[i].r = rx + sx;
                gamer[i].c = ry + sy;
            }
        }

        // 출구 회전
        int x = exit.r;
        int y = exit.c;
        if(sx <= x && x < sx + squareSize && sy <= y && y < sy + squareSize) {
            int ox = x - sx;
            int oy = y - sy;
            int rx = oy;
            int ry = squareSize - ox - 1;
            exit.r = rx + sx;
            exit.c = ry + sy;
        }
    }

}