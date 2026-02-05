class workshop4 {

    interface Foo {
        public boolean dfs_color(int node, int color);
    }

    public boolean isBipartite(final int[][] graph) {
        final int[] visited = new int[graph.length];

        Foo gColor = new Foo() {
            @Override
            public boolean dfs_color(int node, int color) {
                // BUG #1: Incorrectly handling already visited nodes.
                // Instead of returning false if colors mismatch, we only check if it equals
                // current.
                // This could lead to infinite recursion or incorrect true results.
                if (visited[node] != 0) {
                    return true; // Should be: return (visited[node] == color);
                }

                visited[node] = color;

                for (int i = 0; i < graph[node].length; i++) {
                    int neighbor = graph[node][i];
                    // BUG #2: Logic error in neighbor traversal.
                    // Using "color" instead of "-color" for the neighbor.
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