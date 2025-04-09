%PACKAGE_STATEMENT%

import dagger.hilt.android.ViewModelLifecycle

public class %CLASSNAME%(
    private val mediator: %ORIGIN_MEDIATOR%,
    viewModelLifecycle: ViewModelLifecycle,
) : %TARGET_INTERFACE_NAME% by mediator {

    init {
        viewModelLifecycle.addOnClearedListener {
            mediator.%INTERNAL_CLEAN_UP_METHOD%()
        }
    }

}
