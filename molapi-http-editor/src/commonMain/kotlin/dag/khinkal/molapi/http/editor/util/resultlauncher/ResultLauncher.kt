package dag.khinkal.molapi.http.editor.util.resultlauncher

internal interface ResultLauncher<I> {

    fun launch(input: I)
}
