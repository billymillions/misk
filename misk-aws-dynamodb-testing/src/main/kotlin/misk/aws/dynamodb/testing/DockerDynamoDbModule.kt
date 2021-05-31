package misk.aws.dynamodb.testing

import app.cash.tempest.testing.DockerDynamoDbServer
import app.cash.tempest.testing.TestTable
import app.cash.tempest.testing.internal.TestDynamoDbService
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBStreams
import com.google.inject.Provides
import misk.ServiceModule
import misk.dynamodb.DynamoDbHealthCheck
import misk.dynamodb.RequiredDynamoDbTable
import misk.healthchecks.HealthCheck
import misk.inject.KAbstractModule
import javax.inject.Singleton
import kotlin.reflect.KClass

/**
 * Spins up a docker container for testing. It clears the table content before each test starts.
 *
 * Note that this may not be used alongside [InProcessDynamoDbModule]. DynamoDB may execute in Docker or
 * in-process, but never both.
 */
class DockerDynamoDbModule(
  private val tables: List<DynamoDbTable>
) : KAbstractModule() {

  constructor(vararg tables: DynamoDbTable) : this(tables.toList())
  constructor(vararg tables: KClass<*>) : this(tables.map { DynamoDbTable(it) })

  override fun configure() {
    for (table in tables) {
      multibind<DynamoDbTable>().toInstance(table)
    }
    multibind<HealthCheck>().to<DynamoDbHealthCheck>()
    install(ServiceModule<TestDynamoDb>())
  }

  @Provides @Singleton
  fun provideRequiredTables(): List<RequiredDynamoDbTable> =
    tables.map { RequiredDynamoDbTable(it.tableName) }

  @Provides @Singleton
  fun providesTestDynamoDb(): TestDynamoDb {
    return TestDynamoDb(
      TestDynamoDbService.create(
        serverFactory = DockerDynamoDbServer.Factory,
        tables = tables.map { TestTable.create(it.tableClass, it.configureTable) },
        port = null
      )
    )
  }

  @Provides @Singleton
  fun providesAmazonDynamoDB(testDynamoDb: TestDynamoDb): AmazonDynamoDB {
    return testDynamoDb.service.client.dynamoDb
  }

  @Provides @Singleton
  fun providesAmazonDynamoDBStreams(testDynamoDb: TestDynamoDb): AmazonDynamoDBStreams {
    return testDynamoDb.service.client.dynamoDbStreams
  }
}

