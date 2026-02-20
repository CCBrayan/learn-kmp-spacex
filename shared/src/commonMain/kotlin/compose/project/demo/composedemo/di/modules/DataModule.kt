package compose.project.demo.composedemo.di.modules

import compose.project.demo.composedemo.data.local.AppDatabase
import compose.project.demo.composedemo.data.local.AppDatabaseQueries
import compose.project.demo.composedemo.data.local.DriverFactory
import compose.project.demo.composedemo.data.local.ILocalRocketLaunchesDataSource
import compose.project.demo.composedemo.data.local.LocalRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.IRemoteRocketLaunchesDataSource
import compose.project.demo.composedemo.data.remote.RemoteRocketLaunchesDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val dataModule = module {
    single<IRemoteRocketLaunchesDataSource> {
        RemoteRocketLaunchesDataSource(
            get(),
            Dispatchers.IO
        )
    }

    single { get<DriverFactory>().createDriver() }
    single { AppDatabase(get()) }
    
    // Provee explícitamente las queries para que LocalRocketLaunchesDataSource las reciba
    single<AppDatabaseQueries> { get<AppDatabase>().appDatabaseQueries }
    
    single<ILocalRocketLaunchesDataSource> { LocalRocketLaunchesDataSource(get()) }
}
