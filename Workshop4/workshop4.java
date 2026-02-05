class workshop4 {

    interface Foo {
        public boolean dfs_color(int node, int color);
    }

    public boolean isBipartite(final int[][] graph) {
        final int[] visited = new int[graph.length];

        Foo gColor = new Foo() {
            @Override
            public boolean dfs_color(int node, int color) {
                if (visited[node] != 0) {
                    return true;
                }

                visited[node] = color;

                for (int i = 0; i < graph[node].length; i++) {
                    int neighbor = graph[node][i];
                    boolean res = dfs_color(neighbor, color);
                    if (!res)
                        return false;
                }
                return true;
            }
        };

        for (int i = 0; i < graph.length; i++) {
            if (visited[i] == 0) {
                if (!gColor.dfs_color(i, 1))
                    return false;
            }
        }
        return true;
    }
}
