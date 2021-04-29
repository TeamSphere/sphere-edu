package jobs.loaders

import akka.Done
import constants.COURSE_LAST_ID_KEY
import play.api.Logging
import javax.inject.{Inject, Singleton}
import repositories.RedisRepository

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DataLoaderManager @Inject()(
    topicLoader: TopicLoader,
    courseLoader: CourseLoader,
    studentLoader: StudentLoader,
    filterLoader: FilterLoader,
    indexLoader: IndexLoader,
    interestRelationLoader: InterestRelationLoader,
    hasRelationLoader: HasRelationLoader,
    rateRelationLoader: RateRelationLoader,
    studyingRelationLoader: StudyingRelationLoader,
    redisRepository: RedisRepository,
    identityLoader: IdentityLoader
  )(implicit executionContext: ExecutionContext)
    extends Logging {

  def load(): Future[Done] = {
    logger.info("Loading all data into the graph")

    for {
      _ <- topicLoader.loadTopics()
      coursesLength <- courseLoader.loadCoursesToGraph()
      _ <- redisRepository.set(COURSE_LAST_ID_KEY, coursesLength.toString)
      _ <- courseLoader.loadCourses()
      _ <- filterLoader.loadFilters()
      _ <- indexLoader.loadIndexes()
      _ <- hasRelationLoader.loadHasRelations()
      _ <- studentLoader.loadStudentsToGraph()
      _ <- studentLoader.loadStudents()
      _ <- interestRelationLoader.loadInterestRelations()
      _ <- studyingRelationLoader.loadStudyingRelations()
      _ <- rateRelationLoader.loadRateRelations()
      _ <- identityLoader.loadPublicKey()
      // _ <- loadTeachers()
    } yield Done
  }
}
