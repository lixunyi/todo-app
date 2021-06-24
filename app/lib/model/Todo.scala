package lib.model

import ixias.model._
import ixias.util.EnumStatus
import java.time.LocalDateTime

// ToDoを表すモデル
//~~~~~~~~~~~~~~~~~~~~
import Todo._
case class Todo(
	id:           Option[Id],
	category_id:  Category.Id,
	title:        String,
	body:         String,
	state:        Status,
	updatedAt:    LocalDateTime = NOW,
	createdAt:    LocalDateTime = NOW,
	var category: Category = null
) extends EntityModel[Id]

// コンパニオンオブジェクト
//~~~~~~~~~~~~~~~~~~~~~~~~
object Todo {

	val  Id         = the[Identity[Id]]
	type Id         = Long @@ Todo
	type WithNoId   = Entity.WithNoId [Id, Todo]
	type EmbeddedId = Entity.EmbeddedId[Id, Todo]

	// ステータス定義
	//~~~~~~~~~~~~~~~~~
	sealed abstract class Status(val code: Short, val name: String) extends EnumStatus
	object Status extends EnumStatus.Of[Status] {
		case object IS_BEFORE     extends Status(code = 0,   name = "TODO(着手前)")
		case object IS_PROCESSING extends Status(code = 1,   name = "進行中")
		case object IS_COMPLETE   extends Status(code = 2,   name = "完了")
	}

  	// INSERT時のIDがAutoincrementのため,IDなしであることを示すオブジェクトに変換
	def apply(category_id: Category.Id, title: String,body: String): WithNoId = {
		new Entity.WithNoId(
			new Todo(
				id          = None,
				category_id = category_id,
				title       = title,
				body        = body,
				state       = Status.IS_BEFORE
			)
		)
	}
}